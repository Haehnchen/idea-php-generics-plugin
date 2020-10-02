package de.espend.idea.php.quality.phpstan;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.*;
import de.espend.idea.php.quality.CheckstyleQualityToolMessageProcessor;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorConfiguration;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorProjectConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpStanAnnotatorQualityToolAnnotator extends QualityToolAnnotator {
    public static final PhpStanAnnotatorQualityToolAnnotator INSTANCE = new PhpStanAnnotatorQualityToolAnnotator();

    @Override
    protected QualityToolMessageProcessor createMessageProcessor(@NotNull QualityToolAnnotatorInfo qualityToolAnnotatorInfo) {
        return new CheckstyleQualityToolMessageProcessor(qualityToolAnnotatorInfo) {
            @Override
            protected @IntentionFamilyName QualityToolType<PhpStanValidatorConfiguration> getQualityToolType() {
                return PhpStanQualityToolType.getInstance();
            }
        };
    }

    @Override
    protected @Nullable List<String> getOptions(@Nullable String s, @NotNull QualityToolValidationInspection qualityToolValidationInspection, @NotNull Project project) {
        return getCommandLineOptions(s);
    }

    private List<String> getCommandLineOptions(String filePath) {
        ArrayList<String> options = new ArrayList<>();

        options.add("analyse");
        options.add("--error-format=checkstyle");
        options.add(filePath);

        return options;
    }

    @Nullable
    protected QualityToolConfiguration getConfiguration(@NotNull Project project, @NotNull LocalInspectionTool inspection) {
        try {
            return PhpStanValidatorProjectConfiguration.getInstance(project).findSelectedConfiguration(project);
        } catch (QualityToolValidationException e) {
            return null;
        }
    }

    @Override
    protected @NotNull QualityToolType<PhpStanValidatorConfiguration> getQualityToolType() {
        return PhpStanQualityToolType.getInstance();
    }
}
