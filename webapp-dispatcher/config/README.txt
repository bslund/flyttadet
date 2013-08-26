The files present under each subfolder here will be selectively included in the
war file based on the chosen profile (actually, based on the targetEnv property).
The files will be included under WEB-INF/classes.

E.g. if the current profile is dev, all files under config/dev/ will be included
under WEB-INF/classes in the resulting war file.
