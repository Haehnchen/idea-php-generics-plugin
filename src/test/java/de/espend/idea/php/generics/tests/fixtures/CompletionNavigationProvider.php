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

        public function foobar2($a, array $foobar, $c)
        {
            $this->foobar3(null, null, $foobar, null);
        }

        public function foobar3($c, $b, array $x, $z)
        {
            $this->foobar($x);
        }
    }
}
