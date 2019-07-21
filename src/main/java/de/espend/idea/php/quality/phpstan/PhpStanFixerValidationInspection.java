package de.espend.idea.php.quality.phpstan;

import com.intellij.codeInspection.CleanupLocalInspectionTool;
import com.jetbrains.php.tools.quality.QualityToolAnnotator;
import com.jetbrains.php.tools.quality.QualityToolValidationInspection;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpStanFixerValidationInspection extends QualityToolValidationInspection implements CleanupLocalInspectionTool {
    @NotNull
    @Override
    protected QualityToolAnnotator getAnnotator() {
        return PhpStanAnnotatorQualityToolAnnotator.INSTANCE;
    }

    @Override
    public String getToolName() {
        return "PHPStan";
    }
}
