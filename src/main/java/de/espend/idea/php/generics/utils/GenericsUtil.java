package de.espend.idea.php.generics.utils;

import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class GenericsUtil {
    public static boolean isGenericsClass(@NotNull PhpClass phpClass) {
        PhpDocComment phpDocComment = phpClass.getDocComment();
        if(phpDocComment != null) {
            // "@template T"
            // "@template Foo"
            for (String docBlock : Arrays.asList("@template", "@psalm-template")) {
                for (PhpDocTag phpDocTag : phpDocComment.getTagElementsByName(docBlock)) {
                    String tagValue = phpDocTag.getTagValue();
                    if (StringUtils.isNotBlank(tagValue) && tagValue.matches("\\w+")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
