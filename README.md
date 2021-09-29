![Java CI with Maven](https://github.com/enexusde/devtools-tomcat-bypass/workflows/Java%20CI%20with%20Maven/badge.svg)

Warning: This project is discontinued.

# devtools-tomcat-bypass
Chrome DevTools Workspace Bypass-Filter for /META-INF/resources/

If you like to activate the dependency for windows only use this profile in the `pom.xml`:

```
	<profiles>
		<profile>
			<dependencies>
				<dependency>
					<groupId>de.e-nexus</groupId>
					<artifactId>devtools-tomcat-bypass</artifactId>
					<version>0.0.1-SNAPSHOT</version>
				</dependency>
			</dependencies>
			<activation>
				<os>
					<family>windows</family>
				</os>
			</activation>
		</profile>
	</profiles>
```

How to configure: 

1. Start the server and await this message:


```
Mai 14, 2021 11:52:49 VORM. de.e_nexus.web.tomcat.chrome.bypass.ConfigAware getBypassParameter
WARNUNG: Missing property 'servlet.http.58bffe63930c023c54c9613ea73be874' for request 'http://localhost:8080/style.css'.
```
2. Create a file `$user.home$/devtools.properties` and add this line:
`servlet.http.58bffe63930c023c54c9613ea73be874=style.css`

# Discontinued

Since I do use tomcat I could bypass resources using tomcat's `src/main/resources/META-INF/context.xml` having content like this:
```
<Context path="/" copyXML="true" addWebinfClassesResources="true">
	<PreResource base="D:\Workspace\project\src\main\webapp" internalPath="myfile.js"></PreResource>
</Context>
```
