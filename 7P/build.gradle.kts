repositories {
  mavenCentral()
}

plugins {
  java
  // test-logger plugin displays test results in the terminal, and is not strictly necessary.
  id("com.adarshr.test-logger") version "3.1.0"
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

testlogger {
  showStandardStreams = true
}

dependencies {
  implementation("org.apache.commons:commons-lang3:3.12.0")
  testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testImplementation("io.github.java-diff-utils:java-diff-utils:4.12")
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}

sourceSets {
  main {
    java {
      setSrcDirs(listOf("src/main"))
    }
    resources {
      setSrcDirs(listOf("src/resources"))
    }
  }
  test {
    java {
      setSrcDirs(listOf("src/test"))
    }
    resources {
      setSrcDirs(listOf("src/resources"))
    }
  }
}
