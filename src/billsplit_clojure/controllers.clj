(ns billsplit-clojure.controllers
  (:require [billsplit-clojure.logic :as l]
            [billsplit-clojure.adapters :as a]))

(defn create-bill [people params]
  (let [bill     (l/add-people-list-to-bill (l/bill) people)
        products (a/products-wire->internal params)
        who-consumed (a/consumed-wire->internal people params)]
    (l/add-product-list-to-bill bill products who-consumed)))

(defn calculate-bill [bill params]
  (let [tip-value (a/tip-wire->internal params)]
    (-> bill
        (l/calculate-people-costs)
        (l/add-tip tip-value))))
