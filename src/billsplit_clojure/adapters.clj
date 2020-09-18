(ns billsplit-clojure.adapters
  (:require [schema.core :as s]
            [billsplit-clojure.model :as m]))

(def params {:products ["Onion" "Fries"], :quantities [1 1], :values [50 30], :consumed0_0 "on", :consumed1_0 "on", :consumed0_1 "on", :tipswitch "on", :tipvalue 10})

(defn tip-wire->internal
  [{:keys [tipswitch tipvalue]}]
  (when (= "on" tipswitch)
    (/ tipvalue 100.0)))

;testing  - remove later
(tip-wire->internal params)

;(s/defn products-wire->internal
;  [{:keys [products quantities values]}]
;  (map #(zipmap [:name :quantity :price] %)
;       (map vector products quantities values)))

(s/defn products-wire->internal
  [{:keys [products quantities values]}]
  (let [products (->> (map vector products quantities values)
                      (map #(zipmap [:name :quantity :price] %))
                      (map-indexed #(assoc %2 :index %1)))]
    (zipmap (range (count products)) products)))

;testing  - remove later
(products-wire->internal params)

(s/defn consumed-wire->internal
  [people-list params]
  (println params)
  (for [i (range (count people-list))]
    (for [j (range (count people-list))]
      (= "on" (get params (keyword (str "consumed" i "_" j)))))))

(s/defn consumed-wire->internal
  [people {:keys [products] :as params}]
  (println params)
  (let [boolean-consumption-matrix (for [i (range (count products))] ; i are products
                                     (for [j (range (count people))] ; j are people
                                       (= "on" (get params (keyword (str "consumed" i "_" j))))))
        keep-people-who-consumed (fn [people-consumption] (remove false? (map #(and %1 %2) people-consumption people)))
        consumption-matrix (map keep-people-who-consumed boolean-consumption-matrix)]
    (zipmap (range (count products)) consumption-matrix)))

;testing  - remove later
(let [consumed (consumed-wire->internal ["Vitor" "Manuela"] params)]
  consumed)




; Onion: Vitor e Manuela comeram (0 e 1)

; Fries: só 0 comeu (Vitor)

(let [products (products-wire->internal params)
      who-consumed (consumed-wire->internal ["Vitor" "Manuela"] params)]
  (println products)
  (println who-consumed)
  )
