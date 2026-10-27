# Bendegó - Protótipo

Um RPG baseado em turnos.

```
# Windows
# 1. Cria a pasta build
mkdir build 2>nul

# 2. Gera a lista de arquivos fonte Java
dir /s /B src\*.java > lista_fontes.txt

# 3. Compila os arquivos para a pasta build
javac -cp "lib/*" -d build @lista_fontes.txt

# 4. Executa a aplicação (modo debug -d)
java -cp "lib/*;build" main.Main -d
```

```
# Linux / macOS
# 1. Cria a pasta build
mkdir -p build

# 2. Gera a lista de arquivos fonte Java
find src -name "*.java" > lista_fontes.txt

# 3. Compila os arquivos para a pasta build
javac -cp "lib/*" -d build @lista_fontes.txt

# 4. Executa a aplicação (modo debug -d)
java -cp "lib/*:build" main.Main -d
```
