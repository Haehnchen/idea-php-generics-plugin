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
    private static final ExtensionPointName<PsalmValidatorConfigurationProvider> EP_NAME = ExtensionPointName.create("com.jetbrains.php.tools.quality.phpcs.phpCSConfigurationProvider");

    @Nullable
    public static PsalmValidatorConfigurationProvider getInstances() {
        return null;
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