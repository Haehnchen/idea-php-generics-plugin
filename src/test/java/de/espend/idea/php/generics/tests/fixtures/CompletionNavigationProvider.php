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
         * @param array{foobar2: string, foo--__---2FOO2122: string} $foobar
         */
        public function foobar(array $foobar)
        {
        }
    }
}
