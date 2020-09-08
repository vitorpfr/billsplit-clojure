(ns billsplit-clojure.adapters
  (:require [schema.core :as s]
            [billsplit-clojure.model :as m]))

(def params {:tipswitch "on", :quantities [1 1], :consumed0_1 "on", :products ["Onion rings" "Fries"], :tipvalue 10, :consumed0_0 "on", :values [30 50], :consumed1_1 "on", :consumed1_0 "on"})

(defn tip-wire->internal
  [{:keys [tipswitch tipvalue]}]
  (when (= "on" tipswitch)
    (/ tipvalue 100.0)))

(s/defn products-wire->internal
  [{:keys [products quantities values]}]
  (map #(zipmap [:name :quantity :price] %)
        (map vector products quantities values)))

(products-wire->internal params)

(s/defn consumed-wire->internal
  [people-list
   ]
  )