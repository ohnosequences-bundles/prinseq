package ohnosequencesBundles.statika

import ohnosequences.statika._, bundles._, instructions._
import java.io.File


abstract class Prinseq extends Bundle() {

  val usrbin : String = "/usr/bin/"

  val pckgs:Seq [String] = Seq(
    "Test::Simple@0.98",
    "Pod::Parser@1.60",
    "Cwd@3.40",
    "common::sense@3.72",
    "JSON::XS@2.33",
    "JSON@2.57",
    "Getopt::Long@2.39",
    "Pod::Text@3.18",
    "Pod::Usage@1.61",
    "Digest::MD5@2.52",
    "parent@0.228",
    "version@0.9902",
    "MIME::Base64@3.13",
    "Module::Metadata@1.000011",
    "JSON::PP@2.27202",
    "CPAN::Meta::YAML@0.008",
    "Parse::CPAN::Meta@1.4404",
    "CPAN::Meta::Requirements@2.122",
    "CPAN::Meta@2.130880",
    "Perl::OSType@1.003",
    "Module::Build@0.4005",
    "Test::Number::Delta@1.03",
    "ExtUtils::Depends@0.304",
    "ExtUtils::PkgConfig@1.14",
    "Cairo@1.103",
    "http://s3-eu-west-1.amazonaws.com/resources.ohnosequences.com/math-matrixreal/2.08/Math-MatrixReal-2.08.tar.gz",
    "Math::Cephes::Matrix@0.51",
    "Text::SimpleTable@2.03",
    "Want@0.21",
    "http://s3-eu-west-1.amazonaws.com/resources.ohnosequences.com/contextual-return/0.2.1/Contextual-Return-v0.2.1.tar.gz"
  )

  val prinseqUtils : Set[String] = Set(
    "prinseq-graphs-noPCA.pl",
    "prinseq-graphs.pl",
    "prinseq-lite.pl"
  )

  def installCpanmPckgs(pckg: String):Results = Seq ("cpanm", pckg)
  def makeExec(tool: String) : Results = Seq ("chmod", "+x", s"prinseq-lite-0.20.4/${tool}")
  def linkCommand (cmd: String) : Results = Seq("ln", "-sf", new File(s"prinseq-lite-0.20.4/${cmd}").getAbsolutePath,  s"/usr/bin/${cmd}")


  def install: Results = {
    Seq("yum", "install", "-y", "make", "gcc", "tar", "curl", "perl", "perl-Module-Build", "cairo", "cairo-devel") ->-
    Seq("wget", "http://s3-eu-west-1.amazonaws.com/resources.ohnosequences.com/cpanm/1.7102/cpanm", "-O", "cpanm") ->-
    Seq("chmod", "+x", "cpanm") ->-
    Seq("cp", "cpanm", usrbin) ->-
    pckgs.foldLeft[Results](
      Seq("echo", "installing cpanm packages")
    ){(acc, cmd) => acc ->- installCpanmPckgs(cmd)} ->-
    Seq("echo", "cpanm packages installed") ->-
    Seq("cpanm", "--force", "Statistics::PCA@0.0.1") ->-
    Seq("wget", "http://s3-eu-west-1.amazonaws.com/resources.ohnosequences.com/prinseq/0.20.4/prinseq-lite-0.20.4.tar.gz", "-O", "prinseq-lite-0.20.4.tar.gz") ->-
    Seq("tar", "-xvf", "prinseq-lite-0.20.4.tar.gz") ->-
    prinseqUtils.foldLeft[Results](
      Seq("echo", "making prinseq utils executable")
    ){(acc, cmd) => acc ->- makeExec(cmd)} ->-
    prinseqUtils.foldLeft[Results](
      Seq("echo", "linking prinseq executables")
    ){(acc, cmd) => acc ->- linkCommand(cmd)} ->-
    success(s"${bundleName} is installed")
  }

}
