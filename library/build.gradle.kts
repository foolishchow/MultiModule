plugins {
    `kotlin-dsl`
    `maven`
    `java`
}

repositories {
    google()
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("com.android.tools.build:gradle:3.6.3")
}

group = "com.github.foolishchow"

tasks {
    "uploadArchives"(Upload::class) {
        repositories {
            withConvention(MavenRepositoryHandlerConvention::class) {
                mavenDeployer {

                    withGroovyBuilder {
                        "repository"("url" to uri("../PluginTestRepository") )
                    }

                    pom.artifactId = "multi-module-test"
                    pom.groupId = "com.github.foolishchow"
                    pom.version = "0.0.1"

                }
            }
        }
    }
}
