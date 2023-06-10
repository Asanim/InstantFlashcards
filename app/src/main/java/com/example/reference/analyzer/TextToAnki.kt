package com.example.reference.analyzer

import android.database.sqlite.SQLiteDatabase
import java.io.File
import java.util.regex.Pattern
import android.database.Cursor

class TextToAnki {

    private fun countDuplicates(string: String): Pair<ArrayList<Pair<String, Int>>, ArrayList<String>> {
        val charCounts = HashMap<Char, Int>()

        // Count the occurrences of each character
        for (char in string) {
            charCounts[char] = charCounts.getOrDefault(char, 0) + 1
        }

        val duplicates = ArrayList<Pair<String, Int>>() // List for characters with duplicates
        val uniques = ArrayList<String>() // List for characters without duplicates

        // Separate characters into duplicates and uniques
        for ((char, count) in charCounts) {
            if (count > 1) {
                duplicates.add(Pair(char.toString(), count))
            } else {
                uniques.add(char.toString())
            }
        }

        // Sort the duplicates list by the number of duplicates in descending order
        duplicates.sortByDescending { it.second }

        return Pair(duplicates, uniques)
    }

    private fun keepChineseCharacters(string: String): String {
        val pattern = Pattern.compile("[\\u4e00-\\u9fff]+")
        val matcher = pattern.matcher(string)
        val chineseChars = ArrayList<String>()

        while (matcher.find()) {
            chineseChars.add(matcher.group())
        }

        return chineseChars.joinToString("")
    }

    private fun unzipApkg(apkgPath: String) {
        // Code to unzip the APKG file in Kotlin
    }

    private fun getSfldToIdMap(
        databasePath: String,
        keyValue: String = "sfld"
    ): HashMap<String, Int> {
        val db = SQLiteDatabase.openOrCreateDatabase(databasePath, null)
        val query = "SELECT $keyValue, id FROM notes;"
        val cursor: Cursor = db.rawQuery(query, null)

        val sfldToIdMap = HashMap<String, Int>()

        val columnIndexSfld = cursor.getColumnIndex(keyValue)
        val columnIndexId = cursor.getColumnIndex("id")

        while (cursor.moveToNext()) {
            val sfld = if (columnIndexSfld != -1) cursor.getString(columnIndexSfld) else null
            val id = if (columnIndexId != -1) cursor.getInt(columnIndexId) else null

            sfld?.let {
                val sfldExtracted = it.split("<img")[0]
                val parsedSfld = sfldExtracted.replace('\u001F', ' ')
//                sfldToIdMap[parsedSfld] = id
            }
        }

        cursor.close()
        db.close()

        return sfldToIdMap
    }


    private fun printTableHeadingsAndFirstEntry(databasePath: String, tableName: String) {
        val db = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE)

        db.rawQuery("PRAGMA table_info($tableName);", null)?.use { cursor ->
            val columns = mutableListOf<String>()
            val nameColumnIndex = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                val columnName = cursor.getString(nameColumnIndex)
                columns.add(columnName)
            }

            println("Table Headings:")
            println(columns)

            db.rawQuery("SELECT * FROM $tableName LIMIT 1;", null)?.use { entryCursor ->
                if (entryCursor.moveToFirst()) {
                    val entryValues = mutableListOf<String>()
                    for (i in 0 until entryCursor.columnCount) {
                        entryValues.add(entryCursor.getString(i))
                    }

                    println("\nFirst Entry Values:")
                    println(entryValues)
                }
            }
        }

        db.close()
    }


    private fun getCharacterDictionary(
        databasePath: String,
        originalDbPath: String
    ): HashMap<String, Int> {
        unzipApkg(databasePath)

        // DEBUG
        printTableHeadingsAndFirstEntry(originalDbPath, "notes")

        println("getSfldToIdMap")
        val sfldToId = getSfldToIdMap(originalDbPath, "flds")

        return sfldToId
    }

    // Example usage
    fun main() {
        val myString = """
        这个错误提示表明在调用DeleteThingGroup操作时请求中包含的安全令牌无效。

        通常情况下这个错误是由于使用了无效或过期的安全令牌导致的。安全令牌通常用于验证和授权对AWS亚马逊网络服务资源的访问权限。

        要解决这个问题你可以尝试以下步骤

        检查令牌有效性确保你使用的安全令牌是有效的并且没有过期。如果你是使用AWS Identity and Access Management (IAM) 创建的访问密钥可以在AWS控制台的IAM部分进行验证和更新。

        检查令牌权限确保你的安全令牌具有执行DeleteThingGroup操作所需的必要权限。你可以通过IAM角色或用户策略来管理权限。检查相关策略是否正确配置并包含DeleteThingGroup操作的权限。

        检查网络连接确保你的应用程序能够与AWS服务进行通信没有任何网络连接问题。检查网络设置、防火墙或代理配置确保可以正常访问AWS服务。

        如果你仍然遇到问题建议参考AWS官方文档、开发者论坛或联系AWS支持以获取更详细的帮助和指导。
    """
        val apkgPath = "Most_Common_3000_Chinese_Hanzi_Characters.apkg"

        if (File(apkgPath).exists()) {
            println("File path is valid.")
        } else {
            println("File path is invalid.")
        }

        println("keepChineseCharacters")
        val cleanedString = keepChineseCharacters(myString)

        println("countDuplicates")
        val (duplicates, uniques) = countDuplicates(cleanedString)

        val extractionDir = "./${File(apkgPath).nameWithoutExtension}/"
        println(extractionDir)
        val originalDbPath = File(extractionDir, "collection.anki2").path

        println("getCharacterDictionary")
        val sfldToId = getCharacterDictionary(apkgPath, originalDbPath)

        println("Map of sfld to id:")
        for ((sfld, id) in sfldToId) {
            println("sfld: $sfld, id: $id")
        }

        println("\nDuplicates:")
        for ((char, count) in duplicates) {
            println("Character: $char, Duplicates: $count")
        }

        println("\nCharacters without duplicates:")
        for (char in uniques) {
            println("Character: $char")
        }

        println("duplicates")
        println(duplicates)

        val totalCharacters = uniques + duplicates.map { it.first }
        val equalEntries = totalCharacters.filter { it in sfldToId.keys }

        println("\nMatched characters and their corresponding numbers:")
        for (entry in equalEntries) {
            val number = sfldToId[entry]
            println("Character: $entry, Number: $number")
        }
    }
}