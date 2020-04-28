package de.espend.idea.php.quality.psalm.configuration;

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
public class PsalmValidatorConfigurationBaseManager extends QualityToolConfigurationBaseManager<PsalmValidatorConfiguration> {
    public PsalmValidatorConfigurationBaseManager() {
    }

    public static PsalmValidatorConfigurationBaseManager getInstance() {
        return ServiceManager.getService(PsalmValidatorConfigurationBaseManager.class);
    }

    @NotNull
    protected PsalmValidatorConfiguration createLocalSettings() {
        return new PsalmValidatorConfiguration();
    }

    @NotNull
    protected String getQualityToolName() {
        return "Psalm";
    }

    @NotNull
    protected String getOldStyleToolPathName() {
        return "psalm";
    }

    @NotNull
    protected String getConfigurationRootName() {
        return "psalm_settings";
    }

    @Nullable
    protected QualityToolConfigurationProvider<PsalmValidatorConfiguration> getConfigurationProvider() {
        return PsalmValidatorConfigurationProvider.getInstances();
    }

    @Nullable
    protected PsalmValidatorConfiguration loadLocal(Element element) {
        return XmlSerializer.deserialize(element, PsalmValidatorConfiguration.class);
    }
}