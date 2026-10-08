package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

class UserServiceSimulation extends Simulation {

  // ─── Config ───────────────────────────────────────────────────────────────

  val baseUrl = System.getProperty("baseUrl", "http://localhost:8080")
  val users   = System.getProperty("users", "50").toInt
  val rampSec = System.getProperty("ramp", "30").toInt

  // Put your actual JWT token here — get one from POST /auth/login first
  val jwtToken = System.getProperty("jwt", "your-jwt-token-here")

  val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .authorizationHeader(s"Bearer $jwtToken")
    .userAgentHeader("Gatling LoadTest")

  // ─── Feeders ──────────────────────────────────────────────────────────────

  val createUserFeeder = Iterator.continually(Map(
    "firstName" -> s"user_${Random.alphanumeric.take(6).mkString}",
    "lastName"  -> s"last_${Random.alphanumeric.take(4).mkString}",
    "email"     -> s"user_${Random.alphanumeric.take(8).mkString}@test.com",
    "password"  -> "password123"
  ))

  // Infinite circular feeder — never runs out
  val userIdFeeder = Iterator.continually(Iterator.from(1).take(100)).flatten.map(i => Map("userId" -> i))

  // ─── Scenarios ────────────────────────────────────────────────────────────

  // 1. GET /users — list all users
  val getAllUsers = scenario("Get All Users")
    .exec(
      http("GET /users")
        .get("/users")
        .check(status.is(200))
        .check(responseTimeInMillis.lte(2000))
    )

  // 2. GET /users/{id} — get single user
  val getUserById = scenario("Get User By ID")
    .feed(userIdFeeder)
    .exec(
      http("GET /users/{id}")
        .get("/users/#{userId}")
        .check(status.in(200, 404))
        .check(responseTimeInMillis.lte(1000))
    )

  // 3. POST /users — create user
  val createUser = scenario("Create User")
    .feed(createUserFeeder)
    .exec(
      http("POST /users")
        .post("/users")
        .body(StringBody(
          """{"firstName": "#{firstName}", "lastName": "#{lastName}", "email": "#{email}", "password": "#{password}"}"""
        )).asJson
        .check(status.is(200))
        .check(responseTimeInMillis.lte(2000))
        .check(jsonPath("$.id").saveAs("createdUserId"))
    )

  // 4. PUT /users/{id} — update user
  val updateUser = scenario("Update User")
    .feed(userIdFeeder)
    .feed(createUserFeeder)
    .exec(
      http("PUT /users/{id}")
        .put("/users/#{userId}")
        .body(StringBody(
          """{"firstName": "#{firstName}_updated", "lastName": "#{lastName}_updated", "email": "#{email}"}"""
        )).asJson
        .check(status.in(200, 404))
        .check(responseTimeInMillis.lte(2000))
    )

  // 5. DELETE /users/{id} — delete user
  val deleteUser = scenario("Delete User")
    .feed(userIdFeeder)
    .exec(
      http("DELETE /users/{id}")
        .delete("/users/#{userId}")
        .check(status.in(204, 404))
        .check(responseTimeInMillis.lte(1000))
    )

  // 6. GET /users/test — health smoke test
  val smokeTest = scenario("Smoke Test")
    .exec(
      http("GET /users/test")
        .get("/users/test")
        .check(status.is(200))
        .check(bodyString.is("User Service is running!"))
        .check(responseTimeInMillis.lte(500))
    )

  // ─── Mixed realistic scenario ─────────────────────────────────────────────

  val realisticWorkload = scenario("Realistic Workload")
    .feed(createUserFeeder)
    .feed(userIdFeeder)
    .exec(
      http("GET all users")
        .get("/users")
        .check(status.is(200))
    )
    .pause(1.second)
    .exec(
      http("GET user by id")
        .get("/users/#{userId}")
        .check(status.in(200, 404))
    )
    .pause(500.milliseconds)
    .exec(
      http("POST create user")
        .post("/users")
        .body(StringBody(
          """{"firstName": "#{firstName}", "lastName": "#{lastName}", "email": "#{email}", "password": "#{password}"}"""
        )).asJson
        .check(status.is(200))
        .check(jsonPath("$.id").saveAs("newUserId"))
    )
    .pause(1.second)
    .exec(
      http("PUT update user")
        .put("/users/#{newUserId}")
        .body(StringBody(
          """{"firstName": "#{firstName}_updated", "lastName": "#{lastName}_updated", "email": "#{email}"}"""
        )).asJson
        .check(status.in(200, 404))
    )

  // ─── Setup ────────────────────────────────────────────────────────────────

  setUp(
    // Smoke test first — 1 user, instant
    smokeTest.inject(
      atOnceUsers(1)
    ),

    // Read-heavy workload — most traffic in real apps
    getAllUsers.inject(
      rampUsers(users) during (rampSec.seconds)
    ),

    // Single user lookups — high frequency
    getUserById.inject(
      constantUsersPerSec(10) during (rampSec.seconds)
    ),

    // Write operations — lower frequency
    createUser.inject(
      rampUsers(users / 5) during (rampSec.seconds)
    ),

    // Updates — less frequent
    updateUser.inject(
      rampUsers(users / 10) during (rampSec.seconds)
    ),

    // Realistic mixed workload
    realisticWorkload.inject(
      nothingFor(5.seconds),
      rampUsers(users / 2) during (rampSec.seconds)
    )
  )
    .protocols(httpProtocol)
    .assertions(
      // 95% of requests must complete under 2 seconds
      global.responseTime.percentile(95).lte(2000),
      // Success rate must be above 99%
      global.successfulRequests.percent.gte(99),
      // Max response time under 5 seconds
      global.responseTime.max.lte(5000)
    )
}