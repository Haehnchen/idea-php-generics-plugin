# IntelliJ IDEA / PhpStorm PHP Generics

[![Build Status](https://travis-ci.org/Haehnchen/idea-php-generics-plugin.svg?branch=master)](https://travis-ci.org/Haehnchen/idea-php-annotation-plugin)
[![Version](http://phpstorm.espend.de/badge/xxxx/version)](https://plugins.jetbrains.com/plugin/7320)
[![Downloads](http://phpstorm.espend.de/badge/xxxx/downloads)](https://plugins.jetbrains.com/plugin/7320)
[![Downloads last month](http://phpstorm.espend.de/badge/xxxx/last-month)](https://plugins.jetbrains.com/plugin/7320)
[![Donate to this project using Paypal](https://img.shields.io/badge/paypal-donate-yellow.svg)](https://www.paypal.me/DanielEspendiller)


Key         | Value
----------- | -----------
Plugin url  | https://plugins.jetbrains.com/plugin/xxx
Id          | de.espend.idea.php.generic
Changelog   | [CHANGELOG](CHANGELOG.md)


!!! Work in progress !!!

## Support

https://youtrack.jetbrains.com/issue/WI-47158

```php
/**
 * @template T
 */
class Map
{
    /**
     * @param array<string, T>
     */
    public function __construct(array $data = []) {}
    /**
     * @return T
     */
    public function get(string $key) {}
    /**
     * @param T $value
     */
    public function set(string $key, $value): void {}
}
// Automatically inferred as Map<string>
$map = new Map([0 => 'Foo', 1 => 'Bar']);
$map->set(2, true); // Expected string
```


https://youtrack.jetbrains.com/issue/WI-45248


```php
    class Assert
    {
        /**
         * @psalm-template ExpectedType of object
         * @psalm-param class-string<ExpectedType> $class
         * @psalm-assert ExpectedType $value
         */
        public static function isInstanceOf($value, $class, $message = '')
        {
        }
    }
```