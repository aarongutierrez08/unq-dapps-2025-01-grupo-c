import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

class ArchApplicationTest {
    private lateinit var importedClasses: JavaClasses
    private var controller = "..controller.."
    private var service = "..service.."
    private var repository = "..repository.."

    @BeforeEach
    fun setup() {
        importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example.unq_dapps_2025_01_grupo_c")
    }

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {
        noClasses()
            .that().resideInAnyPackage(service, repository)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(controller)
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses)
    }

    @Test
    fun serviceClassesShouldOnlyBeAccessedByController() {
        classes()
            .that().resideInAPackage(service)
            .should().onlyBeAccessed().byAnyPackage(service, controller)
            .check(importedClasses)
    }

    @Test
    fun serviceClassesShouldBeNamedXService() {
        classes()
            .that().resideInAPackage(service)
            .should().haveSimpleNameEndingWith("Service")
            .check(importedClasses)
    }

    @Test
    fun repositoryClassesShouldBeNamedXRepository() {
        classes()
            .that().resideInAPackage(repository)
            .should().haveSimpleNameEndingWith("Repository")
            .check(importedClasses)
    }

    @Test
    fun controllerClassesShouldBeNamedXController() {
        classes()
            .that().resideInAPackage(controller)
            .should().haveSimpleNameEndingWith("Controller")
            .check(importedClasses)
    }

    @Test
    fun fieldInjectionNotUseAutowiredAnnotation() {
        noFields()
            .should().beAnnotatedWith(Autowired::class.java)
            .check(importedClasses)
    }

    @Test
    fun repositoryClassesShouldHaveSpringRepositoryAnnotation() {
        classes()
            .that().resideInAPackage(repository)
            .should().beAnnotatedWith(Repository::class.java)
            .check(importedClasses)
    }

    @Test
    fun serviceClassesShouldHaveSpringServiceAnnotation() {
        classes()
            .that().resideInAPackage(service)
            .should().beAnnotatedWith(Service::class.java)
            .check(importedClasses)
    }

    @Test
    fun controllerClassesShouldHaveSpringRestControllerAnnotation() {
        classes()
            .that().resideInAPackage(controller)
            .should().beAnnotatedWith(RestController::class.java)
            .check(importedClasses)
    }

    @Test
    fun layeredArchitectureShouldBeRespected() {
        layeredArchitecture().consideringOnlyDependenciesInLayers()
            .layer("Controller").definedBy(controller)
            .layer("Service").definedBy(service)
            .layer("Repository").definedBy(repository)
            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
            .check(importedClasses)
    }

}
