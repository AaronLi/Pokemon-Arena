maxLen = 0
with open('possibleNames.txt') as f:
    for i in f:
        if len(i) > maxLen:
            maxLen = len(i)

maxLenPok = 0
with open('allPokemon.txt') as f:
    for i in f:
        name = len(i.split(',')[0])
        if name > maxLenPok:
            maxLenPok = name
print(maxLen+maxLenPok)