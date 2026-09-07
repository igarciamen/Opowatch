package com.igarciamen.watchers;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.igarciamen.watchers");

    @Test
    void entities_live_in_package_model() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(Entity.class)
                .should().resideInAPackage("..model..");
        rule.check(CLASSES);

        System.out.println("[Arquitectura OK] Todas las entidades JPA viven en el paquete ..model..");
    }

    @Test
    void repositories_end_in_Repository_and_live_in_package_repository() {
        ArchRule rule = classes()
                .that().areAssignableTo(JpaRepository.class)
                .should().haveSimpleNameEndingWith("Repository")
                .andShould().resideInAPackage("..repository..");
        rule.check(CLASSES);

        System.out.println("[Arquitectura OK] Todos los repositorios terminan en 'Repository' y viven en ..repository..");
    }

    @Test
    void controllers_end_in_Controller() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(RestController.class)
                .should().haveSimpleNameEndingWith("Controller");
        rule.check(CLASSES);

        System.out.println("[Arquitectura OK] Todos los @RestController terminan en 'Controller'.");
    }

    @Test
    void controllers_are_in_package_controller() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(RestController.class)
                .should().resideInAPackage("..controller..");
        rule.check(CLASSES);

        System.out.println("[Arquitectura OK] Todos los @RestController viven en el paquete ..controller..");
    }

    @Test
    void controllers_do_not_use_repositories() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..repository..");
        rule.check(CLASSES);

        System.out.println("[Arquitectura OK] Ningun controlador depende directamente de un repositorio.");
    }
}