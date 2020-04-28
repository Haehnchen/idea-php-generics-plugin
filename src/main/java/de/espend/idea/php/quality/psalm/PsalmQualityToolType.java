package de.espend.idea.php.quality.psalm;

import com.jetbrains.php.tools.quality.QualityToolType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PsalmQualityToolType extends QualityToolType {
    @NotNull
    @Override
    public String getDisplayName() {
        return "Psalm";
    }
}
