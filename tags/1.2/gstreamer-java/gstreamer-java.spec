Summary:	Java interface to the gstreamer framework
Name:		gstreamer-java
Version:	0.9
Release:	0.2.20081023hg%{?dist}
License:	LGPLv3 and CC-BY-SA
Group:		System Environment/Libraries
URL:		http://code.google.com/p/gstreamer-java/
# The source for this package was pulled from upstream's vcs.  Use the
# following commands to generate the tarball:
#  hg clone -r 537 https://kenai.com/hg/gstreamer-java~mercurial gstreamer-java
#  tar cjvf gstreamer-java-src-%{version}.tar.bz2 --exclude .hg* gstreamer-java
Source:		http://gstreamer-java.googlecode.com/files/%{name}-src-%{version}.tar.bz2
BuildArch:	noarch
BuildRoot:	%{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)
Requires:	java >= 1:1.6.0
Requires:	jpackage-utils
# versioned require since the bug fixed only this version:
# "JNA changed the way type mappers were configured for Callbacks"
#Requires:	jna >= 3.0.7
Requires:	jna >= 3.0.4
# versioned gstreamer required since earlier version don't have features like:
# gst_caps_merge, gst_query_new_latency
Requires:	gstreamer >= 0.10.19
Requires:	gstreamer-plugins-base >= 0.10.19
Conflicts:	gstreamer-plugins-good < 0.10.7
Conflicts:	gstreamer-plugins-ugly < 0.10.7
Conflicts:	gstreamer-plugins-bad < 0.10.6
BuildRequires:	java-devel >= 1:1.6.0
BuildRequires:	jpackage-utils
#BuildRequires:	jna >= 3.0.7
BuildRequires:	jna >= 3.0.4
BuildRequires:	libswt3-gtk2
BuildRequires:	ant >= 1.7.0
BuildRequires:	ant-junit >= 1.7.0
BuildRequires:	junit4
BuildRequires:	gstreamer-devel >= 0.10.19
BuildRequires:	gstreamer-plugins-base-devel >= 0.10.19
BuildRequires:	gstreamer-plugins-good-devel >= 0.10.7
#BuildRequires:	gstreamer-plugins-ugly-devel >= 0.10.7
#BuildRequires:	gstreamer-plugins-bad-devel >= 0.10.6

%description
An unofficial/alternative set of java bindings for the gstreamer multimedia
framework.

%package javadoc
Summary:	Javadocs for %{name}
Group:		Documentation
Requires:	%{name} = %{version}-%{release}
Requires:	jpackage-utils

%description javadoc
This package contains the API documentation for %{name}.

%prep
%setup -q -n %{name}
cp -p src/org/freedesktop/tango/COPYING COPYING.CC-BY-SA
# remove prebuild binaries
find . -name '*.jar' -exec rm {} \;

%build
sed -i -e "s,\(file.reference.jna.jar=\).*,\1$(build-classpath jna)," \
	-e "s,\(file.reference.junit-4.4.jar=\).*,\1$(build-classpath junit4)," \
	-e "s,\(run.jvmargs=-Djna.library.path=\).*,\1%{_libdir}:$(pkg-config --variable=pluginsdir gstreamer-0.10)," nbproject/project.properties
%if 0%{?fedora}
sed -i -e "s,\(file.reference.swt.jar=\).*,\1$(build-classpath swt)," nbproject/project.properties
%else
sed -i -e "s,\(file.reference.swt.jar=\).*,\1$(find %{_libdir} -name swt*.jar 2>/dev/null|sort|head -1)," nbproject/project.properties
%endif
ant

%install
rm -rf %{buildroot}
mkdir -p -m0755 %{buildroot}%{_javadir}
install -m 0644  dist/*.jar	%{buildroot}%{_javadir}

mkdir -p -m0755 %{buildroot}%{_javadocdir}/%{name}
cp -rp dist/javadoc/* %{buildroot}%{_javadocdir}/%{name}

%clean
rm -rf %{buildroot}

%files
%defattr(-,root,root,-)
%{_javadir}/*
%doc CHANGES COPYING* tutorials/*

%files javadoc
%defattr(-,root,root,-)
%{_javadocdir}/%{name}

%changelog
* Sat Oct 25 2008 Levente Farkas <lfarkas@lfarkas.org> - 0.9-0.2.20081023hg
- more spec file cleanup

* Tue Sep  2 2008 Levente Farkas <lfarkas@lfarkas.org> - 0.9
- update to mercurial repo

* Thu Aug  7 2008 Gergo Csontos <gergo.csontos@gmail.com> - 0.9
- remove the manual subpackage and put into the man doc

* Thu Jul 10 2008 Gergo Csontos <gergo.csontos@gmail.com> - 0.8
- Initial release
