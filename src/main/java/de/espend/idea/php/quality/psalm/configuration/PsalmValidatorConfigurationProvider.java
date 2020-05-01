package de.espend.idea.php.quality.psalm.configuration;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.NullableFunction;
import com.jetbrains.php.tools.quality.QualityToolConfigurationProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public abstract class PsalmValidatorConfigurationProvider extends QualityToolConfigurationProvider<PsalmValidatorConfiguration> {
    private static final ExtensionPointName<PsalmValidatorConfigurationProvider> EP_NAME = ExtensionPointName.create("de.espend.idea.php.quality.psalm.psalmConfigurationProvider");

    @Nullable
    public static PsalmValidatorConfigurationProvider getInstances() {
        // make org.jetbrains.plugins.phpstorm-remote-interpreter optional; like done by PhpStorm implementations
        PsalmValidatorConfigurationProvider[] extensions = EP_NAME.getExtensions();
        if (extensions.length > 1) {
            throw new RuntimeException("Several providers for remote Psalm configuration was found");
        }

        return extensions.length == 1 ? extensions[0] : null;
    }

    protected void fillSettingsByDefaultValue(@NotNull PsalmValidatorConfiguration settings, @NotNull PsalmValidatorConfiguration localConfiguration, @NotNull NullableFunction<String, String> preparePath) {
        super.fillSettingsByDefaultValue(settings, localConfiguration, preparePath);

        String toolPath = preparePath.fun(localConfiguration.getToolPath());
        if (StringUtil.isNotEmpty(toolPath)) {
            settings.setToolPath(toolPath);
        }

        settings.setTimeout(localConfiguration.getTimeout());
    }
}