Changelog
=========

# Version names
* 0.x.x: PhpStorm 2020.1+

## 0.6.0
* Provide Psalm inspection description (Daniel Espendiller)
* Higher index version for data object usages since last version (Daniel Espendiller)
* Support remote working directory resolving for quality tools (Daniel Espendiller)
* Psalm remote interpreter (Daniel Espendiller)
* Remove phpcs references (dbrekelmans)
* Fix psalm typo (dbrekelmans)
* Add remote interpreter support for PhpStan quality tool (Daniel Espendiller)
* Show full message output on quality tool checking for PHPStan (Daniel Espendiller)
* Fix possible ArrayIndexOutOfBoundsException in type resolving (Daniel Espendiller)
* Update README.md (Andrew Kovalyov)

## 0.5.1
* Provide inspection description on how to configure PHPStan (Daniel Espendiller)
* Update since build to drop older PhpStorm versions which are not supported (Daniel Espendiller)
* Fix wrong prefix for inspection (Daniel Espendiller)

## 0.5.0
* Add support for Psalm in quality tools inspection (Daniel Espendiller)
* Provide plugin icon (Daniel Espendiller)
* Add support for PHPStan in quality tools inspection (Daniel Espendiller)
* Create description file / Change label for inspections (orklah)
* Add phpstan suffix support for all docblock (Daniel Espendiller)

## 0.4.0
* Wrap all doc tag calls into support al suffixes from static frameworks (Daniel Espendiller)
* Provide psalm return type based on class-string return type templates (Daniel Espendiller)

## 0.3.1
* Just guessing this will fix Issue [#11](https://github.com/Haehnchen/idea-php-generics-plugin/issues/11) [#10](https://github.com/Haehnchen/idea-php-generics-plugin/issues/10) (HenkPoley)

## 0.3.0
* Add inspection for readonly access on property based on @psalm-readonly and @psalm-immutable tag
* Provide a workaround where "@param" lexer is stripping after "array{"

## 0.2.0
* Array-Types: Object-like arrays

## 0.1.0
* Add psalm string-class inspection for arguments
