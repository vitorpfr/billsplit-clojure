(ns billsplit-clojure.logic
  (:require [schema.core :as s]
            [billsplit-clojure.model :as m]))

(s/set-fn-validation! true)

; section 1: add data to bill

; keep this or not?
(s/defn bill :- m/Bill
  []
  {})

(s/defn person :- m/Person
  [name :- s/Str]
  {:name     name
   :products []})

(s/defn add-person :- m/Bill
  [bill :- m/Bill
   person-name :- s/Str]
  (assoc bill person-name (person person-name)))

(s/defn add-people-list :- m/Bill
  [bill :- m/Bill
   people-list :- [s/Str]]
  (reduce add-person bill people-list))

(s/defn product :- m/Product
  [name quantity price id]
  {:name     name
   :quantity quantity
   :price    (bigdec price)
   :fraction nil
   :id       id})

(s/defn add-product-to-person :- m/Bill
  [bill :- m/Bill
   person-name :- s/Str
   product :- m/Product]
  (update-in bill [person-name :products] conj product))

(s/defn add-product-to-bill :- m/Bill
  "Given a list of people who consumed a product, adds a fraction of the product to each person bill"
  [bill :- m/Bill
   person-list :- [s/Str]
   {:keys [quantity] :as product} :- m/Product]
  (let [fractions (repeat (count person-list) (/ quantity (count person-list)))
        product-fractions (zipmap person-list fractions)]
    (reduce-kv (fn [bill person-name fraction]
                 (add-product-to-person bill
                                        person-name
                                        (assoc product :fraction fraction)))
               bill
               product-fractions)))

(s/defn add-products-to-bill
  [bill :- m/Bill
   products :- [m/Product]
   who-consumed]
  (reduce (fn [bill {:keys [id] :as product}]
               (add-product-to-bill bill (get who-consumed id) product))
             bill
             products))

; testing - add-products-to-bill
(let [bill (-> (bill)
               (add-people-list ["Vitor" "Manuela"]))
      products [(product "Onion" 1 50.00 0) (product "Fries" 1 30.00 1)]
      who-consumed {0 ["Vitor" "Manuela"], 1 ["Vitor"]}]
  (add-products-to-bill bill products who-consumed))


; section 2: calculate costs (after bill is ready)

(s/defn calculate-product-cost
  [{:keys [quantity price fraction]} :- m/Product]
  (* quantity price fraction))

(s/defn calculate-person-cost :- m/Person
  [person :- m/Person]
  (let [cost-per-product (map calculate-product-cost (:products person))]
    (assoc person :to-pay (bigdec (reduce + cost-per-product)))))

(defn update-map [m f]
  (reduce-kv (fn [m k v]
               (assoc m k (f v))) {} m))

(s/defn calculate-costs :- m/Bill
  [bill :- m/Bill]
  (update-map bill calculate-person-cost))

;testing calculate-costs -  remove later
(let [bill (-> (bill)
               (add-people-list ["Vitor" "Manuela"]))
      products [(product "Onion" 1 50.00 0) (product "Fries" 1 30.00 1)]
      who-consumed {0 ["Vitor" "Manuela"], 1 ["Vitor"]}
      final-bill (add-products-to-bill bill products who-consumed)]
  (calculate-costs final-bill))
