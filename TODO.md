# To-do list
## Enquanto está sendo desenvolvido:
- Corrigir bugs da tip 
    - Nao deixa preencher se o default é nao-preenchido, ex: desliga tip, volta uma pagina, avança, tenta preencher e nao deixa)
    - Deixa colocar % maiores que 100
    - Visual do input box pode ser melhor
- Stress test: testar parametros errados no form
- Garantir que tem controles de dados faltando

## Após estar funcionando:
- Consertar arredondamento (49.99 por dois vira 24.995, que é arredondado pra 25 pra ambos)
- Migrar JS de jquery pra react
- Transformar consumed0_0 booleans em arrays de arrays pra armazenar todos os booleans, e enviar como hidden element pro form em string (ver msg vini)

## Feito
- Corrigir session (atualmente os nomes de pessoas persistem mesmo entrando de novo no site)
- Implementar check que todos os nomes das pessoas são diferentes antes de prosseguir