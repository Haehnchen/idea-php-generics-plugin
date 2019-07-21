<?php

namespace Foobar {
    class Foo
    {
    }
}

namespace {

    use Foobar\Foo as MyFoo;

    class C
    {
        /**
         * @template T as Exception
         * @param T::class $type
         * @return T
         */
        public static function a(string $type): Exception
        {
            return new $type;
        }

        /**
         * @template T as MyFoo
         * @template K as Foobar\Foo
         * @param T::class $type
         * @psalm-param K::class $type2
         * @return T
         */
        public static function b(string $type, string $type2): InvalidArgumentException
        {
            return self::a($type);
        }
    }
}
