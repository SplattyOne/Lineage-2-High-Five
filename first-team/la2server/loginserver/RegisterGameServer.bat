@echo off
title First-Team: Game Server Registration...
:start
echo Starting Game Server Registration.
echo.
java -server -Xms64m -Xmx64m -cp config/xml;../serverslibs/*; l2ft.loginserver.GameServerRegister

pause
