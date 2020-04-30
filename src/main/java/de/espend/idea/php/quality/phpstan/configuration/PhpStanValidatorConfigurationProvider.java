package de.espend.idea.php.quality.phpstan.configuration;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.NullableFunction;
import com.jetbrains.php.tools.quality.QualityToolConfigurationProvider;
import de.espend.idea.php.quality.phpstan.remote.PhpStanValidatorRemoteConfigurationProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public abstract class PhpStanValidatorConfigurationProvider extends QualityToolConfigurationProvider<PhpStanValidatorConfiguration> {
    @Nullable
    public static PhpStanValidatorConfigurationProvider getInstances() {
        // via PHPStorm is loaded as extension to split requirement for "org.jetbrains.plugins.phpstorm-remote-interpreter"
        // we just use the instance here as UI is not fully working without
        return PhpStanValidatorRemoteConfigurationProvider.INSTANCE;
    }

    protected void fillSettingsByDefaultValue(@NotNull PhpStanValidatorConfiguration settings, @NotNull PhpStanValidatorConfiguration localConfiguration, @NotNull NullableFunction<String, String> preparePath) {
        super.fillSettingsByDefaultValue(settings, localConfiguration, preparePath);

        String toolPath = preparePath.fun(localConfiguration.getToolPath());
        if (StringUtil.isNotEmpty(toolPath)) {
            settings.setToolPath(toolPath);
        }

        settings.setTimeout(localConfiguration.getTimeout());
    }
}