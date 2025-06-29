val jacocoToolVersion = "0.8.13"
val jjwtVersion = "0.11.5"
val springdocVersion = "2.8.6"
val seleniumVersion = "4.33.0"
val mockitoKotlinVersion = "5.4.0"
val hsqldbVersion = "2.7.4"
val okhttpMockWebServerVersion = "4.12.0"
val archunitVersion = "1.4.1"

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.sonarqube") version "6.0.1.5171"
	id("jacoco")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

kotlin {
	jvmToolchain(21)
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
	testImplementation("org.hsqldb:hsqldb:$hsqldbVersion")
	testImplementation("com.squareup.okhttp3:mockwebserver:$okhttpMockWebServerVersion")
	testImplementation("com.tngtech.archunit:archunit-junit5:$archunitVersion")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")
	runtimeOnly("org.postgresql:postgresql")
}

tasks.test {
	useJUnitPlatform {
		excludeTags("scrapping")
	}
}

sonar {
	properties {
		property("sonar.projectKey", "aarongutierrez08_unq-dapps-2025-01-grupo-c")
		property("sonar.organization", "aarongutierrez08")
		property("sonar.host.url", "https://sonarcloud.io")
		property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
		property(
			"sonar.coverage.exclusions",
			"""
            **/config/**,
            **/bootstrap/**,
            **/dto/**,
            **/exceptions/**,
            **/model/**,
            **/repository/**,
            **/security/**,
            **/controller/PlayerController**,
            **/service/WhoScoredService**,
            **/ApplicationKt.class,
            com/example/unq_dapps_2025_01_grupo_c/*.class,
            **/TeamControllerTest,
            **/WhoScoredTest,
            """.trimIndent().replace("\n", ",")
		)
	}
}

jacoco {
	toolVersion = jacocoToolVersion
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
}

tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.sonar {
	dependsOn(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.required.set(true)
		html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
	}

	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				exclude(
					"**/config/**",
					"**/bootstrap/**",
					"**/dto/**",
					"**/exceptions/**",
					"**/model/**",
					"**/repository/**",
					"**/security/**",
					"**/controller/PlayerController**",
					"**/service/WhoScoredService*",
					"**/ApplicationKt.class",
					"com/example/unq_dapps_2025_01_grupo_c/*.class",
					"**/TeamControllerTest",
					"**/WhoScoredTest",
				)
			}
		})
	)
}