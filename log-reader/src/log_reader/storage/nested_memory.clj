(ns log-reader.storage.nested-memory
  (:require [log-reader.nested-storage :as st]))

(def ^:private +storage+ (atom {}))

(defn store-entry [{:keys [trace] :as entry}]
  (reduce (fn [path {:keys [id] :as trace-step}]
            (let [path (conj path id)]
              (swap! +storage+ update-in path #(assoc % :callinfo (dissoc trace-step :id)))
              (conj path :children)))
          [:children] trace)
  (let [path (->> (map :id trace)
                  (interleave (repeat :children))
                  vec)]
    (swap! +storage+ assoc-in (conj path :message) (dissoc entry :trace)))
  @+storage+)

(defn lookup [trace-ids]
  (let [path (-> (interleave (repeat :children) trace-ids)
                 vec)]
    (some-> (get-in @+storage+ path)
            (update :children #(map (fn [[k v]]
                                      (conj (vec trace-ids) k))
                                    %)))))
