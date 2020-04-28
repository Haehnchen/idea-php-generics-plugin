package de.espend.idea.php.quality.phpstan.configuration;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.util.xmlb.XmlSerializer;
import com.jetbrains.php.tools.quality.QualityToolConfigurationBaseManager;
import com.jetbrains.php.tools.quality.QualityToolConfigurationProvider;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpStanValidatorConfigurationBaseManager extends QualityToolConfigurationBaseManager<PhpStanValidatorConfiguration> {
    public PhpStanValidatorConfigurationBaseManager() {
    }

    public static PhpStanValidatorConfigurationBaseManager getInstance() {
        return ServiceManager.getService(PhpStanValidatorConfigurationBaseManager.class);
    }

    @NotNull
    protected PhpStanValidatorConfiguration createLocalSettings() {
        return new PhpStanValidatorConfiguration();
    }

    @NotNull
    protected String getQualityToolName() {
        return "PhpStan";
    }

    @NotNull
    protected String getOldStyleToolPathName() {
        return "phpstan";
    }

    @NotNull
    protected String getConfigurationRootName() {
        return "phpstan_settings";
    }

    @Nullable
    protected QualityToolConfigurationProvider<PhpStanValidatorConfiguration> getConfigurationProvider() {
        return PhpStanValidatorConfigurationProvider.getInstances();
    }

    @Nullable
    protected PhpStanValidatorConfiguration loadLocal(Element element) {
        return XmlSerializer.deserialize(element, PhpStanValidatorConfiguration.class);
    }
}