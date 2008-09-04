Summary:	Java interface to the gstreamer framework
Name:		gstreamer-java
Version:	1.0
Release:	1%{?dist}
License:	GNU Lesser General Public License
Group:		System Environment/Libraries
URL:		http://code.google.com/p/gstreamer-java/
Vendor:		Wayne Meissner
Source:		http://gstreamer-java.googlecode.com/files/%{name}-src-%{version}.tar.bz2
BuildArch:	noarch
BuildRoot:	%{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)
BuildRequires:	java-devel >= 1:1.6.0
BuildRequires:	jpackage-utils
BuildRequires:	ant >= 1.7.0
BuildRequires:	ant-junit >= 1.7.0
BuildRequires:	junit4
Requires:	java >= 1:1.6.0
Requires:	jpackage-utils
Requires:	jna
Requires:	gstreamer >= 0.10.19
Requires:	gstreamer-plugins-base >= 0.10.19
Requires:	gstreamer-plugins-good >= 0.10.7
#Requires:	gstreamer-plugins-ugly >= 0.10.7
#Requires:	gstreamer-plugins-bad >= 0.10.6

%description
An unofficial/alternative set of java bindings for the gstreamer multimedia framework.

%package javadoc
Summary:	Javadocs for %{name}
Group:		Development Documentation
Requires:	%{name} = %{version}-%{release}
Requires:	jpackage-utils

%description javadoc
This package contains the API documentation for %{name}.

%prep
%setup -q

%build
#export CLASSPATH=$(build-classpath jna)
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
%doc tutorials/*

%files javadoc
%defattr(-,root,root,-)
%{_javadocdir}/%{name}

%changelog
* Tue Sep  2 2008 Levente Farkas <lfarkas@lfarkas.org> - 1.0
- update to 1.0

* Thu Aug  7 2008 Gergo Csontos <gergo.csontos@gmail.com> - 0.9
- remove the manual subpackage and put into the man doc

* Thu Jul 10 2008 Gergo Csontos <gergo.csontos@gmail.com> - 0.8
- Initial release
