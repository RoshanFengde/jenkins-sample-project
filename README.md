# jenkins-sample-project

A minimal Maven + Selenium + TestNG project, built purely to learn Jenkins
pipelines without the complexity of a full framework.

- Target site: https://the-internet.herokuapp.com/login (a public site made
  for Selenium practice)
- Runs headless Chrome (required for Jenkins/Docker — no display available)
- WebDriverManager auto-downloads the matching chromedriver — no manual
  driver setup needed on any machine, including inside Jenkins

## Run locally

```bash
mvn clean test
```

## Push to GitHub (required before Jenkins can pull it)

```bash
cd jenkins-sample-project
git init
git add .
git commit -m "Initial commit: Jenkins learning sample project"
git branch -M main
git remote add origin https://github.com/<your-username>/jenkins-sample-project.git
git push -u origin main
```
