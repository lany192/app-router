import org.gradle.api.JavaVersion
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions

apply(plugin = "maven-publish")

afterEvaluate {
    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.github.lany192.router"
                artifactId = project.name
                version = "1.0.0"
                afterEvaluate {
                    if (plugins.hasPlugin("com.android.application") || plugins.hasPlugin("com.android.library")) {
                        // AGP 的 withSourcesJar() 已提供 sources jar
                        from(components["release"])
                    } else if (plugins.hasPlugin("java")) {
                        from(components["java"])
                        artifact(tasks.named("sourcesJar"))
                        artifact(tasks.named("javadocJar"))
                    }
                }
            }
        }
        repositories {
            mavenLocal()
            maven {
                name = "local_repository"
                url = uri("${rootProject.rootDir}/repository")
            }
//            maven {
//                isAllowInsecureProtocol = true
//                url = uri("http://localhost:5001/repository/maven-releases/")
//                credentials {
//                    username = "admin"
//                    password = "dev123456"
//                }
//            }
        }
    }

    if (plugins.hasPlugin("java") &&
        !plugins.hasPlugin("com.android.application") &&
        !plugins.hasPlugin("com.android.library")
    ) {
        val sourceSets = extensions.getByType<SourceSetContainer>()
        val classesTask = tasks.named("classes")
        val javadocTask = tasks.named<Javadoc>("javadoc")

        tasks.register<Jar>("sourcesJar") {
            dependsOn(classesTask)
            archiveClassifier.set("sources")
            from(sourceSets.getByName("main").allSource)
        }

        tasks.register<Jar>("javadocJar") {
            dependsOn(javadocTask)
            archiveClassifier.set("javadoc")
            from(javadocTask.map { it.destinationDirectory.get().asFile })
        }
    }

    if (JavaVersion.current().isJava8Compatible) {
        allprojects {
            tasks.withType<Javadoc>().configureEach {
                // 源码为 UTF-8（含中文注释），显式指定编码避免使用平台默认编码
                options.encoding = "UTF-8"
                (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
            }
        }
    }
}