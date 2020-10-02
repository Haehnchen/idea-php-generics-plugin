package de.espend.idea.php.quality.phpstan.configuration;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.util.xmlb.XmlSerializer;
import com.jetbrains.php.tools.quality.QualityToolConfigurationBaseManager;
import com.jetbrains.php.tools.quality.QualityToolType;
import de.espend.idea.php.quality.phpstan.PhpStanQualityToolType;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpStanValidatorConfigurationBaseManager extends QualityToolConfigurationBaseManager<PhpStanValidatorConfiguration> {
    public PhpStanValidatorConfigurationBaseManager() {
    }

    @Override
    protected @NotNull QualityToolType<PhpStanValidatorConfiguration> getQualityToolType() {
        return PhpStanQualityToolType.getInstance();
    }

    public static PhpStanValidatorConfigurationBaseManager getInstance() {
        return ServiceManager.getService(PhpStanValidatorConfigurationBaseManager.class);
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
    protected PhpStanValidatorConfiguration loadLocal(Element element) {
        return XmlSerializer.deserialize(element, PhpStanValidatorConfiguration.class);
    }
}