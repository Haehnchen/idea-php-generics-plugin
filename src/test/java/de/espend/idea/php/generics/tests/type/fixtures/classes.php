<?php

namespace Foobar
{
    class Foobar
    {
        public function getFoo() {}
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
     * @psalm-param class-string<T> $class2
     * @return T
     */
    function instantiator2(string $class, string $class2, string $class3) {
        return new $class();
    }
}


namespace Extended\Implementations
{
    /**
     * @template T
     */
    class MyContainer
    {
        /** @var T */
        private $value;

        /** @param T $value */
        public function __construct($value) {
            $this->value = $value;
        }

        /** @psalm-return T */
        public function getValue() {
            return $this->value;
        }
    }

    class Foobar
    {
        public function getFoobar(){}
    }
}

namespace Extended\Classes
{
    use Extended\Implementations\MyContainer;

    /**
     * @extends \Extended\Implementations\MyContainer<\Extended\Implementations\Foobar>
     */
    class MyExtendsImpl extends MyContainer
    {
    }
}