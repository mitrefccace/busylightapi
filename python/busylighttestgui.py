# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'busylight.ui'
#
# Created by: PyQt5 UI code generator 5.12.2
#
# WARNING! All changes made in this file will be lost!

from PyQt5 import QtCore, QtGui, QtWidgets, QtTest


class Ui_MainWindow(object):
    def setupUi(self, MainWindow):
        MainWindow.setObjectName("MainWindow")
        MainWindow.resize(600, 500)
        MainWindow.setStyleSheet("QPushButton {\n"
"    background-color: red\n"
"    border: 2px solid #555;\n"
"    border-radius: 20px;\n"
"    border-style: outset;\n"
"    background: qradialgradient(\n"
"        cx: 0.3, cy: -0.4, fx: 0.3, fy: -0.4,\n"
"        radius: 1.35, stop: 0 #fff, stop: 1 #888\n"
"        );\n"
"    padding: 5px;\n"
"    }\n"
"\n"
"")
        self.centralwidget = QtWidgets.QWidget(MainWindow)
        self.centralwidget.setObjectName("centralwidget")
        self.gridLayoutWidget = QtWidgets.QWidget(self.centralwidget)
        self.gridLayoutWidget.setGeometry(QtCore.QRect(20, 14, 361, 111))
        self.gridLayoutWidget.setObjectName("gridLayoutWidget")
        self.gridLayout = QtWidgets.QGridLayout(self.gridLayoutWidget)
        self.gridLayout.setContentsMargins(0, 0, 0, 0)
        self.gridLayout.setObjectName("gridLayout")
        self.label_6 = QtWidgets.QLabel(self.gridLayoutWidget)
        self.label_6.setObjectName("label_6")
        self.gridLayout.addWidget(self.label_6, 3, 0, 1, 1)
        self.comboBox_2 = QtWidgets.QComboBox(self.gridLayoutWidget)
        self.comboBox_2.setObjectName("comboBox_2")
        self.comboBox_2.addItem("")
        self.gridLayout.addWidget(self.comboBox_2, 3, 1, 1, 1)
        self.programStatusText = QtWidgets.QLabel(self.gridLayoutWidget)
        self.programStatusText.setText("")
        self.programStatusText.setAlignment(QtCore.Qt.AlignCenter)
        self.programStatusText.setObjectName("programStatusText")
        self.gridLayout.addWidget(self.programStatusText, 0, 1, 1, 1)
        self.label_5 = QtWidgets.QLabel(self.gridLayoutWidget)
        self.label_5.setObjectName("label_5")
        self.gridLayout.addWidget(self.label_5, 2, 0, 1, 1)
        self.comboBox = QtWidgets.QComboBox(self.gridLayoutWidget)
        self.comboBox.setObjectName("comboBox")
        self.comboBox.addItem("")
        self.gridLayout.addWidget(self.comboBox, 2, 1, 1, 1)
        self.label = QtWidgets.QLabel(self.gridLayoutWidget)
        self.label.setObjectName("label")
        self.gridLayout.addWidget(self.label, 0, 0, 1, 1)
        self.agentStatusText = QtWidgets.QLabel(self.gridLayoutWidget)
        self.agentStatusText.setText("")
        self.agentStatusText.setAlignment(QtCore.Qt.AlignCenter)
        self.agentStatusText.setObjectName("agentStatusText")
        self.gridLayout.addWidget(self.agentStatusText, 1, 1, 1, 1)
        self.label_3 = QtWidgets.QLabel(self.gridLayoutWidget)
        self.label_3.setObjectName("label_3")
        self.gridLayout.addWidget(self.label_3, 1, 0, 1, 1)
        self.gridLayoutWidget_2 = QtWidgets.QWidget(self.centralwidget)
        self.gridLayoutWidget_2.setGeometry(QtCore.QRect(200, 130, 351, 88))
        self.gridLayoutWidget_2.setObjectName("gridLayoutWidget_2")
        self.gridLayout_2 = QtWidgets.QGridLayout(self.gridLayoutWidget_2)
        self.gridLayout_2.setContentsMargins(0, 0, 0, 0)
        self.gridLayout_2.setObjectName("gridLayout_2")
        self.testButton = QtWidgets.QPushButton(self.gridLayoutWidget_2)
        self.testButton.setObjectName("testButton")
        self.gridLayout_2.addWidget(self.testButton, 0, 1, 1, 1)
        self.startButton = QtWidgets.QPushButton(self.gridLayoutWidget_2)
        self.startButton.setObjectName("startButton")
        self.gridLayout_2.addWidget(self.startButton, 0, 0, 1, 1)
        self.exitButton = QtWidgets.QPushButton(self.gridLayoutWidget_2)
        self.exitButton.setObjectName("exitButton")
        self.gridLayout_2.addWidget(self.exitButton, 0, 2, 1, 1)
        self.checkBox = QtWidgets.QCheckBox(self.gridLayoutWidget_2)
        self.checkBox.setChecked(True)
        self.checkBox.setObjectName("checkBox")
        self.gridLayout_2.addWidget(self.checkBox, 2, 0, 1, 1)
        self.busylightDetector = QtWidgets.QLabel(self.gridLayoutWidget_2)
        self.busylightDetector.setObjectName("busylightDetector")
        self.gridLayout_2.addWidget(self.busylightDetector, 1, 0, 1, 1)
        self.busyLight = QtWidgets.QPushButton(self.centralwidget)
        self.busyLight.setGeometry(QtCore.QRect(180, 250, 141, 141))
        self.busyLight.setMouseTracking(False)
        self.busyLight.setAutoFillBackground(False)
        self.busyLight.setStyleSheet("QPushButton {\n"
                                    "    \n"
                                    "    alternate-background-color: rgb(85, 255, 127);\n"
                                    "    background-color: rgb(0, 255, 0);\n"
                                    "    color: #111;\n"
                                    "    border: 5px solid #555;\n"
                                    "    border-radius: 70px;\n"
                                    "    border-style: outset;\n"
                                    "    background: qradialgradient(\n"
                                    "        cx: 0.3, cy: -0.4, fx: 0.3, fy: -0.4,\n"
                                    "        radius: 1.35, stop: 0 #fff, stop: 1 #888\n"
                                    "        );\n"
                                    "    padding: 5px;\n"
                                    "    }\n"
                                    "\n"
                                    "")
        self.busyLight.setText("")
        self.busyLight.setFlat(False)
        self.busyLight.setObjectName("busyLight")
        self.verticalLayoutWidget = QtWidgets.QWidget(self.centralwidget)
        self.verticalLayoutWidget.setGeometry(QtCore.QRect(380, 280, 171, 80))
        self.verticalLayoutWidget.setObjectName("verticalLayoutWidget")
        self.verticalLayout = QtWidgets.QVBoxLayout(self.verticalLayoutWidget)
        self.verticalLayout.setContentsMargins(0, 0, 0, 0)
        self.verticalLayout.setObjectName("verticalLayout")
        self.readyRadioButton = QtWidgets.QRadioButton(self.verticalLayoutWidget)
        self.readyRadioButton.setObjectName("readyRadioButton")
        self.verticalLayout.addWidget(self.readyRadioButton)
        self.busyRadioButton = QtWidgets.QRadioButton(self.verticalLayoutWidget)
        self.busyRadioButton.setObjectName("busyRadioButton")
        self.verticalLayout.addWidget(self.busyRadioButton)
        self.unavailableRadioButton = QtWidgets.QRadioButton(self.verticalLayoutWidget)
        self.unavailableRadioButton.setObjectName("unavailableRadioButton")
        self.verticalLayout.addWidget(self.unavailableRadioButton)
        MainWindow.setCentralWidget(self.centralwidget)
        self.menubar = QtWidgets.QMenuBar(MainWindow)
        self.menubar.setGeometry(QtCore.QRect(0, 0, 600, 22))
        self.menubar.setObjectName("menubar")
        self.menuFile = QtWidgets.QMenu(self.menubar)
        self.menuFile.setObjectName("menuFile")
        self.menuEdit = QtWidgets.QMenu(self.menubar)
        self.menuEdit.setObjectName("menuEdit")
        self.menuHelp = QtWidgets.QMenu(self.menubar)
        self.menuHelp.setObjectName("menuHelp")
        MainWindow.setMenuBar(self.menubar)
        self.statusbar = QtWidgets.QStatusBar(MainWindow)
        self.statusbar.setObjectName("statusbar")
        MainWindow.setStatusBar(self.statusbar)
        self.action_Exit = QtWidgets.QAction(MainWindow)
        self.action_Exit.setObjectName("action_Exit")
        self.actionEdit_menu = QtWidgets.QAction(MainWindow)
        self.actionEdit_menu.setObjectName("actionEdit_menu")
        self.actionAbout = QtWidgets.QAction(MainWindow)
        self.actionAbout.setObjectName("actionAbout")
        self.menuFile.addAction(self.action_Exit)
        self.menuEdit.addAction(self.actionEdit_menu)
        self.menuHelp.addAction(self.actionAbout)
        self.menubar.addAction(self.menuFile.menuAction())
        self.menubar.addAction(self.menuEdit.menuAction())
        self.menubar.addAction(self.menuHelp.menuAction())

        self.retranslateUi(MainWindow)
        QtCore.QMetaObject.connectSlotsByName(MainWindow)

        self.agentStatusText.setText("Unknown")
        self.programStatusText.setText("Not Running")

        self.startButton.clicked.connect(self.start_program)
        self.exitButton.clicked.connect(self.exit_program)
        self.checkBox.clicked.connect(self.busylight_detect)

    def busylight_detect(self):
        if self.checkBox.isChecked():
            self.busylightDetector.setText("BusyLight Detected")
            self.programStatusText.setText("Running")
            if self.readyRadioButton.isChecked():
                self.ready_action_selected()
            elif self.busyRadioButton.isChecked():
                self.busy_action_selected()
            elif self.unavailableRadioButton.isChecked():
                self.unavailable_action_selected()
            else:
                self.agentStatusText.setText("Unknown")
                self.busyLight.setStyleSheet("QPushButton {\n"
                                             "    alternate-background-color: rgb(85, 255, 127);\n"
                                             "    background-color: rgb(0, 255, 0);\n"
                                             "    color: #111;\n"
                                             "    border: 2px solid #555;\n"
                                             "    border-radius: 70px;\n"
                                             "    border-style: outset;\n"
                                             "    background: qradialgradient(\n"
                                             "        cx: 0.3, cy: -0.4, fx: 0.3, fy: -0.4,\n"
                                             "        radius: 1.35, stop: 0 #fff, stop: 1 #888\n"
                                             "        );\n"
                                             "    padding: 5px;\n"
                                             "    }\n"
                                             "\n"
                                             "")
        else:
            self.busylightDetector.setText("BusyLight Not Detected")
            self.agentStatusText.setText("Unknown")
            self.programStatusText.setText("Not Running")
            self.busyLight.setStyleSheet("QPushButton {\n"
                                         "    alternate-background-color: rgb(85, 255, 127);\n"
                                         "    background-color: rgb(0, 255, 0);\n"
                                         "    color: #111;\n"
                                         "    border: 2px solid #555;\n"
                                         "    border-radius: 70px;\n"
                                         "    border-style: outset;\n"
                                         "    background: qradialgradient(\n"
                                         "        cx: 0.3, cy: -0.4, fx: 0.3, fy: -0.4,\n"
                                         "        radius: 1.35, stop: 0 #fff, stop: 1 #888\n"
                                         "        );\n"
                                         "    padding: 5px;\n"
                                         "    }\n"
                                         "\n"
                                         "")

    def start_program(self):
        self.programStatusText.setText("Running")

        self.testButton.clicked.connect(self.test_program)
        self.readyRadioButton.clicked.connect(self.ready_action_selected)               #busylight ready
        self.busyRadioButton.clicked.connect(self.busy_action_selected)                 #busylight busy
        self.unavailableRadioButton.clicked.connect(self.unavailable_action_selected)   #busylight unavailable

    def exit_program(self):
        sys.exit()

    def test_program(self):
        self.agentStatusText.setText("partying")

        for i in range(0, 3):
            self.busyLight.setStyleSheet("QPushButton {\n"
                                         "    background-color: rgb(255, 0, 0);\n"
                                         "    border: 2px solid #555;\n"
                                         "    border-radius: 70px;\n"
                                         "    border-style: outset;\n"
                                         "    padding: 5px;\n"
                                         "    }\n"
                                         "\n"
                                         "")
            QtTest.QTest.qWait(100)
            self.busyLight.setStyleSheet("QPushButton {\n"
                                         "    background-color: rgb(255, 128, 0);\n"
                                         "    border: 2px solid #555;\n"
                                         "    border-radius: 70px;\n"
                                         "    border-style: outset;\n"
                                         "    padding: 5px;\n"
                                         "    }\n"
                                         "\n"
                                         "")
            QtTest.QTest.qWait(100)
            self.busyLight.setStyleSheet("QPushButton {\n"
                                         "    background-color: rgb(255, 255, 0);\n"
                                         "    border: 2px solid #555;\n"
                                         "    border-radius: 70px;\n"
                                         "    border-style: outset;\n"
                                         "    padding: 5px;\n"
                                         "    }\n"
                                         "\n"
                                         "")
            QtTest.QTest.qWait(100)
            self.busyLight.setStyleSheet("QPushButton {\n"
                                         "    background-color: rgb(0, 255, 0);\n"
                                         "    border: 2px solid #555;\n"
                                         "    border-radius: 70px;\n"
                                         "    border-style: outset;\n"
                                         "    padding: 5px;\n"
                                         "    }\n"
                                         "\n"
                                         "")
            QtTest.QTest.qWait(100)
            self.busyLight.setStyleSheet("QPushButton {\n"
                                         "    background-color: rgb(0, 0, 255);\n"
                                         "    border: 2px solid #555;\n"
                                         "    border-radius: 70px;\n"
                                         "    border-style: outset;\n"
                                         "    padding: 5px;\n"
                                         "    }\n"
                                         "\n"
                                         "")
            QtTest.QTest.qWait(100)
            self.busyLight.setStyleSheet("QPushButton {\n"
                                         "    background-color: rgb(127, 0, 255);\n"
                                         "    border: 2px solid #555;\n"
                                         "    border-radius: 70px;\n"
                                         "    border-style: outset;\n"
                                         "    padding: 5px;\n"
                                         "    }\n"
                                         "\n"
                                         "")
            QtTest.QTest.qWait(100)

        if self.readyRadioButton.isChecked():
            self.ready_action_selected()
        elif self.busyRadioButton.isChecked():
            self.busy_action_selected()
        elif self.unavailableRadioButton.isChecked():
            self.unavailable_action_selected()
        else:
            self.agentStatusText.setText("Unknown")
            self.busyLight.setStyleSheet("QPushButton {\n"
                                         "    alternate-background-color: rgb(85, 255, 127);\n"
                                         "    background-color: rgb(0, 255, 0);\n"
                                         "    color: #111;\n"
                                         "    border: 2px solid #555;\n"
                                         "    border-radius: 70px;\n"
                                         "    border-style: outset;\n"
                                         "    background: qradialgradient(\n"
                                         "        cx: 0.3, cy: -0.4, fx: 0.3, fy: -0.4,\n"
                                         "        radius: 1.35, stop: 0 #fff, stop: 1 #888\n"
                                         "        );\n"
                                         "    padding: 5px;\n"
                                         "    }\n"
                                         "\n"
                                         "")

    def ready_action_selected(self):
        self.busyLight.setStyleSheet("QPushButton {\n"
                                     "    background-color: rgb(0, 255, 0);\n"
                                     "    border: 2px solid #555;\n"
                                     "    border-radius: 70px;\n"
                                     "    border-style: outset;\n"
                                     "    padding: 5px;\n"
                                     "    }\n"
                                     "\n"
                                     "")
        self.agentStatusText.setText("Available")

    def busy_action_selected(self):
        self.busyLight.setStyleSheet("QPushButton {\n"
                                     "    background-color: rgb(255, 255, 0);\n"
                                     "    border: 2px solid #555;\n"
                                     "    border-radius: 70px;\n"
                                     "    border-style: outset;\n"
                                     "    padding: 5px;\n"
                                     "    }\n"
                                     "\n"
                                     "")
        self.agentStatusText.setText("Busy")

    def unavailable_action_selected(self):
        self.busyLight.setStyleSheet("QPushButton {\n"
                                     "    background-color: rgb(255, 0, 0);\n"
                                     "    border: 2px solid #555;\n"
                                     "    border-radius: 70px;\n"
                                     "    border-style: outset;\n"
                                     "    padding: 5px;\n"
                                     "    }\n"
                                     "\n"
                                     "")
        self.agentStatusText.setText("Unavailable")

    def retranslateUi(self, MainWindow):
        _translate = QtCore.QCoreApplication.translate
        MainWindow.setWindowTitle(_translate("MainWindow", "ACE Direct BusyLight"))
        self.label_6.setText(_translate("MainWindow", "Product:"))
        self.comboBox_2.setItemText(0, _translate("MainWindow", "PRODUCT_OMEGA_ID"))
        self.label_5.setText(_translate("MainWindow", "Vendor:"))
        self.comboBox.setItemText(0, _translate("MainWindow", "PLENOM"))
        self.label.setText(_translate("MainWindow", "Status:"))
        self.label_3.setText(_translate("MainWindow", "Agent Status:"))
        self.testButton.setText(_translate("MainWindow", "Test"))
        self.startButton.setText(_translate("MainWindow", "Start"))
        self.exitButton.setText(_translate("MainWindow", "Exit"))
        self.checkBox.setText(_translate("MainWindow", "Show Icon"))
        self.busylightDetector.setText(_translate("MainWindow", "BusyLight Detected"))
        self.readyRadioButton.setText(_translate("MainWindow", "Agent Ready"))
        self.busyRadioButton.setText(_translate("MainWindow", "Agent Busy"))
        self.unavailableRadioButton.setText(_translate("MainWindow", "Agent Unavailable"))
        self.menuFile.setTitle(_translate("MainWindow", "File"))
        self.menuEdit.setTitle(_translate("MainWindow", "Edit"))
        self.menuHelp.setTitle(_translate("MainWindow", "Help"))
        self.action_Exit.setText(_translate("MainWindow", " &Exit"))
        self.actionEdit_menu.setText(_translate("MainWindow", "Edit menu"))
        self.actionAbout.setText(_translate("MainWindow", "About"))




if __name__ == "__main__":
    import sys
    app = QtWidgets.QApplication(sys.argv)
    MainWindow = QtWidgets.QMainWindow()
    ui = Ui_MainWindow()
    ui.setupUi(MainWindow)
    MainWindow.show()
    sys.exit(app.exec_())
