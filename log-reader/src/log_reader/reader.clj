(ns log-reader.reader
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))


(defn read-stream [in]
  (let [xf (mapcat (fn [line]
                     (let [parsed (with-in-str line
                                    (->> (repeatedly #(try
                                                        (edn/read {:eof ::eof} *in*)
                                                        (catch Throwable e
                                                          ::invalid)))
                                         (take-while #(not= % ::eof))
                                         (remove #(= % ::invalid))
                                         doall))]
                       (if (empty? parsed)
                         [line]
                         parsed))))]
    (into [] xf (-> in io/reader line-seq))))
