import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.io.InputStream

class JsonObjectTest {

    private val filePath = "../Kson/src/main/test/domates"
    private val ins: InputStream = File(filePath).inputStream()
    private val str = ins.bufferedReader().use { it.readText() }
    private val parser = JsonObject(str)
    @Test
    fun getValues() {
        val test = LinkedHashMap<String,Any>()
        test["itag"] = 18
        test["url"] = "https://rr5---sn-u0g3uxax3-n14e.googlevideo.com/videoplayback?expire=1706889002&ei=yrq8ZZjzFeG36dsP2pr6oAU&ip=88.231.108.32&id=o-APK3SJGh-xjZTjOAkfKtnuO5a3O-OSA-Om_6I2ZG7tSF&itag=18&source=youtube&requiressl=yes&xpc=EgVo2aDSNQ%3D%3D&mh=i1&mm=31%2C29&mn=sn-u0g3uxax3-n14e%2Csn-nv47lns7&ms=au%2Crdu&mv=m&mvi=5&pl=21&initcwndbps=921250&siu=1&spc=UWF9f1o3LNVv4_6e__fOAkmUU4Q1JEdydX1ZMvXqGvZxLz_AWh9MB8w77c9_&vprv=1&svpuc=1&mime=video%2Fmp4&ns=X9vBlv48mE8q_oK4aESE2YwQ&cnr=14&ratebypass=yes&dur=1733.090&lmt=1697782896912370&mt=1706867091&fvip=4&fexp=24007246&c=WEB&txp=5318224&n=xodt4eIJCmpUxav5S&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cxpc%2Csiu%2Cspc%2Cvprv%2Csvpuc%2Cmime%2Cns%2Ccnr%2Cratebypass%2Cdur%2Clmt&sig=AJfQdSswRAIgLd4EQIM_D7Exyu4QL45CVkMbL4EAjG04dJnAujE4VQ0CICQJ-34N77AFj88ZqthwPQhkiH9MraMmVPU0pDPxuiIG&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AAO5W4owRAIgW5E17GAWeqFBr7X4ssUvxtFvyLzL0vNkolN_qOd3agwCICxZvxEj-W0l8jlETtyd-18IY_L_B132SSHELgix97CZ"
        test["mimeType"] = """video/mp4; codecs=\"avc1.42001E, mp4a.40.2\""""
        test["bitrate"] = 203069
        test["width"] =  "640"
        test["height"] =  "360"
        test["lastModified"] =  "1697782896912370"
        test["quality"] = "medium"
        test["fps"] =  "false"
        test["qualityLabel"] =  "360p"
        test["projectionType"] =  "RECTANGULAR"
        test["audioQuality"] =  "AUDIO_QUALITY_LOW"
        test["approxDurationMs"] =  "1733090"
        test["audioSampleRate"] = "44100"
        test["audioChannels"] = 2

        for((k,_) in test){
            assertEquals(parser.values[k],test[k])
        }
    }

    @Test
    fun testObjectInsideOfObject(){
        val test = LinkedHashMap<String,Any>()
        test["start"] = "0"
        test["end"] = "219"
        for((k,_) in test){
            assertEquals((parser.values["initRange"] as JsonObject).values[k],test[k])
        }
    }
}