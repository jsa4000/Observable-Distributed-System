{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "micro.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default short app name to use for resource naming.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "micro.fullname" -}}
{{- $name := default "micro" .Values.appNameOverride -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create an uppercase app name to use for environment variables.
*/}}
{{- define "micro.envname" -}}
{{- $name := default "micro" .Values.appNameOverride -}}
{{- printf "%s_%s" .Release.Name $name | upper | replace "-" "_" | trimSuffix "_" -}}
{{- end -}}

{{/*
Create a name to use for datasource variables.
*/}}
{{- define "micro.database" -}}
{{- if .Values.datasource.host -}}
    {{ default "default" .Values.datasource.host }}
{{- else -}}
    {{- printf "%s-%s" .Release.Name .Values.datasource.type | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{/*
Create an uppercase release prefix to use for environment variables.
*/}}
{{- define "micro.envrelease" -}}
{{- printf "%s" .Release.Name | upper | replace "-" "_" | trimSuffix "_" -}}
{{- end -}}