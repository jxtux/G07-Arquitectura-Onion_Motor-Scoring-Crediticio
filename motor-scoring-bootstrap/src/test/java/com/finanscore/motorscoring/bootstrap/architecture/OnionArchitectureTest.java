package com.finanscore.motorscoring.bootstrap.architecture;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
class OnionArchitectureTest {
    private static final String ROOT = "com.finanscore.motorscoring";
    @Test
    void elDominioNoDebeDependerDeFrameworksNiCapasExternas() {
        var classes = new ClassFileImporter().importPackages(ROOT);
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta..",
                        "org.hibernate..",
                        "..application..",
                        "..infrastructure..",
                        "..presentation..",
                        "..bootstrap.."
                );
        rule.check(classes);
    }
    @Test
    void laAplicacionNoDebeDependerDeInfraestructuraPresentacionOBootstrap() {
        var classes = new ClassFileImporter().importPackages(ROOT);
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..infrastructure..", "..presentation..", "..bootstrap..");
        rule.check(classes);
    }
}
