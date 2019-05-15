(ns log-reader.nested-storage)

(defprotocol NestedStorage
  (store [this ks data])
  (lookup [this ks]))

(defn store-data [this ks data]
  (store this ks {::data data}))

(defn lookup-data [this ks]
  (some-> (lookup this ks)
          ::data))

(defn lookup-children [this ks]
  (some-> (lookup this ks)
          ::children))
