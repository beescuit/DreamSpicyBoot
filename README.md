<p align="center">
<br>
<img src="https://perfectdreams.net/assets/img/perfectdreams_logo.png">
<br>
  <a href="https://perfectdreams.net"><img src="https://img.shields.io/badge/website-perfectdreams-blue.svg"></a>
<a href="https://perfectdreams.net/discord"><img src="https://img.shields.io/badge/discord-perfectdreams-7289da.svg"></a>
<a href="https://perfectdreams.net/loja"><img src="https://img.shields.io/badge/donate-perfectdreams-yellow.svg"></a>
<a href="https://circleci.com/gh/PerfectDreams/DreamSpicyBoot"><img src="https://circleci.com/gh/PerfectDreams/DreamSpicyBoot.svg?style=shield"></a>
<a href="https://mrpowergamerbr.com/"><img src="https://img.shields.io/badge/website-mrpowergamerbr-blue.svg"></a>
<a href="https://github.com/PerfectDreams/DreamSpicyBoot/LICENSE"><img src="https://img.shields.io/badge/license-AGPL%20v3-orange.svg"></a>
</p>
<h1 align="center">DreamSpicyBoot</h1>
<p align="center">🌶️ Deixando o startup do seu servidor mais apimentado. — Gerenciador de plugins, auto updater e muito mais! Hmmmm, Spicy Calamari!</p>

<p align="center">🦑🌶️ https://youtu.be/6zGCKhfXPVo 🌶🦑️</p>

DreamSpicyBoot é um gerenciador de inicialização de servidores para o [PerfectDreams](https://perfectdreams.net), originalmente criado para automaticamente atualizar plugins do [PerfectDreams](https://github.com/PerfectDreams) diretamente do CircleCI para o servidor, ele foi expandido para:

* Gerenciar plugins automaticamente
* Atualizar plugins diretamente pelo CircleCI
* Atualizar a JAR do servidor automaticamente
* Permitir customização das flags de inicialização
* Ativar o JRebel em servidores específicos
* E muito mais!

A ideia do DreamSpicyBoot surgiu ao administrar plugins, já que muitas vezes, ao alterar uma JAR enquanto o servidor está rodando, pode causar problemas no plugin devido as diferenças nas classes.

Usar o DreamSpicyBoot é simples, primeiro você precisa colocar o DreamSpicyBoot em alguma pasta e criar um `config.json`.
```
{
   "pluginsFolder":"/home/servers/perfectdreams/plugins_paradise/",
   "extraFlags":"-Dserver={{serverName}}",
   "classpathJars": [ "/home/servers/perfectdreams/plugins_paradise/kotlin/*" ],
   "jrebelFlags":"-Drebel.remoting_port={{jrebelPort}} -agentpath:/home/jrebel/lib/libjrebel64.so -Drebel.remoting_plugin=true"
}
```
`pluginsFolder`: Aonde o DreamSpicyBoot irá salvar plugins baixados.
`extraFlags`: JVM flags que serão adicionadas no script de inicialização do servidor.
`classpathJars`: JARs que deverão ser colocadas na classpath.
`jrebelFlags`: Flags que serão usadas pelo JRebel, caso esteja ativado no servidor.

Após configurar o DreamSpicyBoot, você deverá criar uma configuração chamada `server_config.json` na pasta do seu servidor.
```
{
   "serverName": "PerfectDreams Lobby",
   "platformType": "PAPER",
   "serverVersion": "1.12.2",
   "autoUpdate": true,
   "deletePluginsOnBoot": true,
   "flags": "-Xmx512M -Xms512M -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:MaxGCPauseMillis=100 -XX:+DisableExplicitGC -XX:TargetSurvivorRatio=90 -XX:G1NewSizePercent=50 -XX:G1MaxNewSizePercent=80 -XX:InitiatingHeapOccupancyPercent=10 -XX:G1MixedGCLiveThresholdPercent=50 -XX:+AggressiveOpts -XX:+AlwaysPreTouch",
   "enableJRebel": true,
   "jrebelPort": 54549,
   "plugins":[
      {
         "name":"DreamCore",
         "autoUpdate":true,
         "updateFrom":"CIRCLECI",
         "sourceJarPattern":"DreamCore-latest",
         "storedJarName":"DreamCore-{{build}}.jar",
         "jarName":"DreamCore.jar"
      },
      ...
      {
         "name":"PlugMan",
         "autoUpdate":false,
         "sourceJarPattern":"PlugMan",
         "jarName":"PlugMan.jar"
      }
   ]
}
```

`serverName`: Nome do servidor.
`platformType`: Plataforma que o servidor utiliza.
`serverVersion`: Versão do servidor.
`autoUpdate`: Se o servidor irá atualizar automaticamente a JAR da plataforma escolhida.
`deletePluginsOnBoot`: Se o DreamSpicyBoot deve deletar todas as JARs da pasta de plugins ao iniciar o servidor.
`flags`: JVM flags.
`enableJRebel`: Se o JRebel deve estar ativado no servidor.
`jrebelPort`: Porta que o JRebel deverá utilizar
`plugins`: Uma lista com todos os plugins que o servidor utiliza.

```
{
    "name":"DreamCore",
    "organization": "PerfectDreams",
    "autoUpdate":true,
    "updateFrom":"CIRCLECI",
    "sourceJarPattern":"DreamCore-latest",
    "storedJarName":"DreamCore-{{build}}.jar",
    "jarName":"DreamCore.jar"
}
```
```
{
    "name":"ProtocolSupport",
    "autoUpdate":false,
    "sourceJarPattern":"ProtocolSupport",
    "jarName":"ProtocolSupport.jar"
}
```

`name`: Nome do plugin.
`organization`: Organização no GitHub do plugin.
`autoUpdate`: Se o plugin será atualizado automaticamente.
`updateFrom`: Fonte do plugin.
`sourceJarPattern`: Pattern RegEx que será utilizado para encontrar o plugin na `pluginFolder` do DreamSpicyBoot.
`storedJarName`: Como o DreamSpicyBoot irá salvar o nome da JAR no `pluginFolder`.
`jarName`: Nome do plugin no `plugins`.

Após criar as configurações necessárias, crie um arquivo chamado `start.sh` e inicie ele pelo bash
```
java -Xmx256M -Xms256M -DserverRoot=$PWD -jar /home/servers/perfectdreams/dreamspicyboot/DreamSpicyBoot-1.0-SNAPSHOT-jar-with-dependencies.jar

sh start0.sh

sh start.sh
```

E se tudo der certo...
![https://mrpowergamerbr.com/uploads/2018-06-24_22-08-14.gif](https://mrpowergamerbr.com/uploads/2018-06-24_22-08-14.gif)

## Como Ajudar?
Existem vários repositórios [na nossa organização](https://github.com/PerfectDreams) de várias partes do PerfectDreams, caso você queria contribuir em outras partes do PerfectDreams, siga as instruções no `README.md` de cada repositório!

### Como Doar?

Mesmo que você não saiba programar, você pode ajudar no desenvolvimento do PerfectDreams comprando vantagens em nossos servidores! https://perfectdreams.net/loja

Você também pode doar para a [Loritta](https://loritta.website/support), a mascote do PerfectDreams! 😊

### Como Usar?

Você também pode compilar o projeto e usar em outros lugares, mas lembrando:
* Eu não irei dar suporte caso você queria usar o plugin no seu servidor sem dar nada em troca para o PerfectDreams, lembre-se, a licença do projeto é [AGPL v3](https://github.com/PerfectDreams/DreamSpicyBoot/LICENSE), você é **obrigado a deixar todas as suas alterações no projeto públicas**.
* Eu não irei ficar explicando como arrumar problemas no seu projeto, você está por sua conta e risco.
* Eu irei dar suporte caso você queria compilar o projeto para ajudar o PerfectDreams.
* Lembrando que nossos projetos precisam de setups e workflows específicos, você **não irá conseguir ysar** nossos projetos apenas compilando e usando.
* Existem várias coisas "hard coded" no projeto, ou seja, você terá que editar o código-fonte dela e recompilar, afinal, o projeto foi criado apenas para ser utilizado no PerfectDreams então você terá que fazer algumas modificações no código-fonte dela para funcionar. ;)
* Você não pode utilizar o nome "PerfectDreams" ou o nome do projeto na sua versão

Mas se você quiser mesmo hospedar a Loritta, siga os seguintes passos:
1. Tenha o MongoDB instalado na sua máquina.
2. Tenha o JDK 8 (ou superior) na sua máquina.
3. Tenha o Git Bash instalado na sua máquina.
4. Tenha o Maven instalado na sua máquina com o `PATH` configurado corretamente. (para que você possa usar `mvn install` em qualquer pasta e o `JAVA_HOME`, para que o `mvn install` funcione)
5. Tenha o IntelliJ IDEA instalado na sua máquina.
6. Tenha um servidor de Minecraft rodando [Paper](https://github.com/PaperMC/Paper) na última versão disponível, para transformar sonhos em realidade, nossos projetos sempre utilizam a última versão disponível no momento que o projeto foi criado.
6. Faça ```git clone https://github.com/PerfectDreams/DreamSpicyBoot.git``` em alguma pasta no seu computador.
7. Agora, usando o PowerShell (ou o próprio Git Bash), entre na pasta criada e utilize `mvn install`
8. Após terminar de compilar, vá na pasta `target` e pegue a JAR do projeto.
9. Coloque na pasta de plugins junto com todas as dependências que o projeto precise.
10. Após terminar de configurar, inicie o servidor e, se tudo der certo, ela irá iniciar e você poderá usar os comandos dela! 🎉

### Pull Requests
No seu Pull Request, você deverá seguir o meu estilo de código bonitinho que eu faço, é recomendado que você coloque comentários nas partes do seu código para que seja mais fácil na hora da leitura.

Caso o seu código possua texto, você é obrigado a utilizar o sistema de localização da Loritta, para que o seu Pull Request possa ser traduzido para outras linguagens, ou seja, após criar o seu Pull Request, crie um Pull Request no repositório de linguagens da Loritta com as keys necessárias.

O seu código não pode ser algo "gambiarra", meu código pode ter algumas gambiarras mas isto não significa que você também deve encher o PerfectDreams com mais gambiarras no seu Pull Request.

Você precisa pensar "será que alguém iria utilizar isto?", se você criar um comando que só seja útil para você, provavelmente eu irei negar o seu Pull Request.

## Licença

O código-fonte deste projeto está licenciado sob a [GNU Affero General Public License v3.0](https://github.com/LorittaBot/Loritta/blob/master/LICENSE)

PerfectDreams é © MrPowerGamerBR — Todos os direitos reservados

A personagem Loritta é © MrPowerGamerBR & PerfectDreams — Todos os direitos reservados

Ao utilizar o projeto você aceita os [termos de uso da Loritta](https://loritta.website/privacy) e os [termos de uso do PerfectDreams](https://perfectdreams.net/privacy).