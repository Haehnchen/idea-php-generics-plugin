<?php

namespace Foo
{
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

    /**
     * @psalm-template X
     */
    class PsalmMap
    {
        /**
         * @param array<string, X>
         */
        public function __construct(array $data = []) {}
        /**
         * @return X
         */
        public function get(string $key) {}
        /**
         * @param X $value
         */
        public function set(string $key, $value): void {}
    }

    /**
     * @template ZzzA
     */
    class Zzz
    {
        /**
         * @param array<string, ZzzA>
         */
        public function __construct(array $data = []) {}
        /**
         * @return ZzzA
         */
        public function get(string $key) {}
        /**
         * @param ZzzA $value
         */
        public function set(string $key, $value): void {}
    }

    /**
     * @template
     */
    class Bar
    {
        /**
         * @param array<string, ZZZ>
         */
        public function __construct(array $data = []) {}
        /**
         * @return X
         */
        public function get(string $key) {}
        /**
         * @param X $value
         */
        public function set(string $key, $value): void {}
    }

}

namespace Instantiator\Foobar
{
    class Foobar
    {
        /**
         * @template T
         * @psalm-param class-string<T> $class
         * @return T
         */
        function _barInstantiator(string $class) {
            return new $class();
        }
    }
}

namespace
{
    /**
     * @template T
     * @psalm-param class-string<T> $class
     * @return T
     */
    function instantiator(string $class) {
        return new $class();
    }

    /**
     * @template T
     * @param class-string<T> $class
     * @return T
     */
    function instantiatorParam(string $class) {
        return new $class();
    }

    /**
     * @template T
     * @param class-string<T> $class
     * @psalm-return T
     */
    function instantiatorReturn(string $class) {
        return new $class();
    }

    /**
     * @phpstan-template T
     * @phpstan-param class-string<T> $class
     * @phpstan-return T
     */
    function instantiatorPhpStan(string $class) {
        return new $class();
    }
}
