package de.espend.idea.php.quality.psalm;

import com.intellij.codeInspection.CleanupLocalInspectionTool;
import com.jetbrains.php.tools.quality.QualityToolAnnotator;
import com.jetbrains.php.tools.quality.QualityToolValidationInspection;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PsalmValidationInspection extends QualityToolValidationInspection implements CleanupLocalInspectionTool {
    @NotNull
    @Override
    protected QualityToolAnnotator getAnnotator() {
        return PsalmAnnotatorQualityToolAnnotator.INSTANCE;
    }

    @Override
    public String getToolName() {
        return "Psalm";
    }
}
