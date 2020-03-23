(ns log-reader.reader
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))


(defn read-stream-xf []
  (letfn [(parse [line]
            (with-in-str line
              (->> (repeatedly #(try
                                  (edn/read {:eof ::eof} *in*)
                                  (catch Throwable e
                                    ::invalid)))
                   (take-while #(not= % ::eof))
                   (remove #(= % ::invalid))
                   doall)))]

    (comp (mapcat (fn [line]
                    (let [parsed (parse line)]
                      (if (empty? parsed)
                        [line]
                        parsed))))
          (remove #(and (string? %)
                        (re-matches #"\s*" %))))))
