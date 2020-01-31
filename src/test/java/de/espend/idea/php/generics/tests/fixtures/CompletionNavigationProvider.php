<?php

namespace
{
    class Foobar
    {
    }

    class Bar
    {
        /**
         * @psalm-param array{foobar: string, foo: Foobar, ?bar: int | string} $foobar
         */
        public function foobar(array $foobar)
        {
        }
    }
}
