(ns clj-ruuvi.url-decoder
  (:import java.util.Base64))

; Functions for converting RuuviTag URL to values
; Protocols: https://github.com/ruuvi/ruuvi-sensor-protocols
; Inspired and helped by https://github.com/ttu/ruuvitag-sensor

(defonce base-pressure 50000)

(defn bytes-to-ushort
  "Converts byte array (size of 2) to unsigned short.
  Clojure does not support unsigned values (because of JVM) so returned value
  is actually integer."
  [& bytes]
    (+
     (bit-shift-left (first bytes) 8)
     (second bytes) ))

(defn to-unsigned-bytes
  "Converts collection of signed bytes to vector of unsigned ones (integers
  for real)"
  [bytes]
  (map #(mod % 256) bytes))

(defn decode
  "Decodes bas64 string or byte array to unsigned byte array"
  [data]
  (to-unsigned-bytes
    (.decode
      (Base64/getUrlDecoder)
      (if (> (count data) 8) (subs data 0 8) data))))

(defn get-humidity
  "Converts bytes to humidity.
  one lsb is 0.5%, e.g. 128 is 64%"
  [decoded]
  (* (second decoded) 0.5))

(defn get-temperature
  "Converts bytes to temperature
  MSB is sign, next 7 bits are decimal value"
  [decoded]
  (let [base (nth decoded 2) fraction (nth decoded 3) ]
    (*
     (+ (bit-and base 127) (/ fraction 100.0))
     (if (= (bit-and (bit-shift-right base 7) 1) 0) 1 -1))))

(defn get-pressure
  "Converts bytes to pressure.
  Most Significant Byte first, value - 50kPa"
  [decoded]
  (/
   (+ (bytes-to-ushort (nth decoded 4) (nth decoded 5)) base-pressure) 100.00))

(defn get-identifier
  [encoded]
  (when (> (count encoded) 8) (subs encoded 8)))

(defn decode-to-map
  "Decode url encoded ruuvi tag data to map"
  [encoded]
  (let [decoded (decode encoded)]
    {:humidity (get-humidity decoded)
     :temperature (get-temperature decoded)
     :pressure (get-pressure decoded)
     :identifier (get-identifier encoded)}))

