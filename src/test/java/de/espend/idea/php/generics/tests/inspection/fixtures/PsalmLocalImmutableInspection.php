<?php


namespace
{
    class PsalmReadOnly {
        /**
         * @psalm-readonly
         */
        public string $readOnly;

        public string $write;
    }

    /**
     * @psalm-immutable
     */
    class PsalmImmutable {
        public string $readOnly;
        public string $write;
    }
}
