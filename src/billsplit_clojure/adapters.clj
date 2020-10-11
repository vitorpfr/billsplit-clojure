(ns billsplit-clojure.adapters
  (:require [schema.core :as s]
            [billsplit-clojure.model :as m]))

; test params - remove after tests are written
;(def params {:products ["Onion" "Fries"], :quantities ["1" "1"], :values ["50" "30"], :consumed0_0 "on", :consumed1_0 "on", :consumed0_1 "on", :tipswitch "on", :tipvalue "10"})

(defn tip-wire->internal
  [{:keys [tipswitch tipvalue]}]
  (when (= "on" tipswitch)
    (/ (Integer/parseInt tipvalue) 100.0)))

(s/defn ^:private quantity-wire->internal [quantity]
  (Integer/parseInt quantity))

(s/defn ^:private value-wire->internal [value]
  (bigdec value))

(s/defn products-wire->internal
  [{:keys [products quantities values]}]
  (->> (map vector products (map quantity-wire->internal quantities) (map value-wire->internal values))
       (map #(zipmap [:name :quantity :price] %))
       (map-indexed #(assoc %2 :id %1))))

(s/defn consumed-wire->internal
  [people {:keys [products] :as params}]
  (let [boolean-consumption-matrix (for [i (range (count products))] ; i are products
                                     (for [j (range (count people))] ; j are people
                                       (= "on" (get params (keyword (str "consumed" j "_" i)))))) ; consumed fields follow pattern consumed[person]_[product]
        keep-people-who-consumed (fn [people-consumption] (remove false? (map #(and %1 %2) people-consumption people)))
        consumption-matrix (map keep-people-who-consumed boolean-consumption-matrix)]
    (zipmap (range (count products)) consumption-matrix)))

(s/defn value-internal->wire
  [value]
  (str "R$ " (format "%.2f" value)))
