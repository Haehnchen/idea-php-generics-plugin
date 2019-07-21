<?php

namespace Foobar {
    class Foo{}
}

namespace {
    use Foo as MyFoo;

    class C
    {
        /**
         * @param string $type
         * @return Exception
         */
        public static function a(string $type): Exception
        {
            return new $type;
        }


        /**
         * @param string $type
         * @return InvalidArgumentException
         */
        public static function b(string $type): InvalidArgumentException
        {
            return self::a($type);
        }
    }
}


