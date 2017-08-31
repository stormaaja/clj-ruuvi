# clj-ruuvi

A Clojure library for reading RuuviTag data.

## Usage

For example in repl:

```
user=> (use 'clj-ruuvi.url-decoder :reload)
nil
user=> (decode-to-map "BHAVAMFci")
{:humidity 56.0, :temperature 21.0, :pressure 995.0, :identifier "i"}
```

## License

Copyright © 2017 Matti Ahinko

Distributed under the Eclipse Public License either version 1.0.
