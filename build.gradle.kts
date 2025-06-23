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
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("org.seleniumhq.selenium:selenium-java:4.31.0")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation ("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework:spring-webflux")
	implementation("io.micrometer:micrometer-core")
	implementation("io.micrometer:micrometer-registry-prometheus")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
	testImplementation("org.hsqldb:hsqldb:2.7.4")
	testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.hsqldb:hsqldb:2.7.4")
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
			"**/service/WhoScoredService**",
            **/ApplicationKt.class,
            com/example/unq_dapps_2025_01_grupo_c/*.class,
			**/TeamControllerTest,
			**/WhoScoredTest,
            """.trimIndent().replace("\n", ",")
		)
	}
}

jacoco {
	toolVersion = "0.8.13"
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