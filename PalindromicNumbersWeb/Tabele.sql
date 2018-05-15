CREATE TABLE Visitor
(
	VisitorGUID TEXT PRIMARY KEY,
	VisitCount INTEGER NOT NULL
);

CREATE TABLE History
(
	HistoryID INT IDENTITY PRIMARY KEY,
	FromRange INTEGER NOT NULL,
	ToRange INTEGER NOT NULL,
	PalindromesCount INTEGER NOT NULL,
	NonPalindromesCount INTEGER NOT NULL,
	VisitorGUID TEXT NOT NULL
);